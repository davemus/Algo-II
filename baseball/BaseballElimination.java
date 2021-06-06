import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/*
    This is a solution to problem of math team elimination in baseball.
    Team is mathematically eliminated, if there are no possible ways for it to
    win group, it is inside. The solution depends not only on number of wins,
    loses and remaining matches, but on with whom this remaining matches will be
    held.

    For more information please refer to specification link:
    https://coursera.cs.princeton.edu/algs4/assignments/baseball/specification.php

    To reader: sorry for hard to read code. I understand, that reading such
    a mess is a problem. But I don't want to contribute more of my Sunday hours
    to this. At least it works.
 */

public class BaseballElimination {
    private static final int WINS_IDX = 0;
    private static final int LOSES_IDX = 1;
    private static final int REMAINING_IDX = 2;
    private static final int IDX_OF_ZERO_COMMAND_IN_STATS = 3;

    private final int pTeamsCount;
    private final String[] pTeams;
    private final int[][] pStats;
    private final HashMap<String, Integer> teamToIdx;
    private final boolean[] eliminated;
    private final HashMap<String, Set<String>> pCertificateOfElimination;

    public BaseballElimination(String filename) {
        /*
            Read input.
            Input has nextFormat:

            numberOfTeams
            NameOfTeam1 WinsCountOfTeam1 LosesCountOfTeam1 RemainingMatchesCountOfTeamN RemMatchesCountWithTeam1 ... RemMatchesCountWithTeamN
            ...
            NameOfTeamN WinsCountOfTeamN LosesCountOfTeamN RemainingMatchesCountOfTeamN RemMatchesCountWithTeam1 ... RemMatchesCountWithTeamN
         */
        In in = new In(filename);
        pTeamsCount = in.readInt();
        pTeams = new String[pTeamsCount];
        teamToIdx = new HashMap<String, Integer>();
        int statsLength = IDX_OF_ZERO_COMMAND_IN_STATS + pTeamsCount;
        pStats = new int[pTeamsCount][statsLength];
        eliminated = new boolean[pTeamsCount];
        pCertificateOfElimination = new HashMap<>();

        for (int i = 0; i < pTeamsCount; i++) {
            pTeams[i] = in.readString();
            teamToIdx.put(pTeams[i], i);
            for (int j = 0; j < statsLength; j++) {
                pStats[i][j] = in.readInt();
            }
        }

        /*
            flow network stores vertices as idx
            pre calculation of what vertex with idx mean inside flow network

            idx of artificial source vertex - 0
            next (pTeamsCount * pTeamsCount - pTeamsCount) / 2 vertices are matches
            next pTeamsCount - 1 indexes are companies
            next 1 idx is artificial sink. Let n will be pTeamsCount, then
            we have 1 + n * n / 2 + n / 2 - 1 + 1 = 1 + n * (n + 1) / 2
            vertices in flow network
         */
        int currVertexIdx = 1; // 0 for source
        int[] teamToVertexIdx = new int[pTeamsCount];
        int[][] gamesWithToVertexIdx = new int[pTeamsCount][pTeamsCount];
        for (int teamIdx = 0; teamIdx < pTeamsCount; teamIdx++) {
            teamToVertexIdx[teamIdx] = currVertexIdx;
            currVertexIdx++;
        }
        for (int firstTeamIdx = 0; firstTeamIdx < pTeamsCount; firstTeamIdx++) {
            for (int secondTeamIdx = firstTeamIdx; secondTeamIdx < pTeamsCount; secondTeamIdx++) {
                gamesWithToVertexIdx[firstTeamIdx][secondTeamIdx] = currVertexIdx;
                gamesWithToVertexIdx[secondTeamIdx][firstTeamIdx] = currVertexIdx;
                currVertexIdx++;
            }
        }

        trivialElimination();

        final int flowNetworkVerticesCount = currVertexIdx;
        FlowNetwork network;
        List<FlowEdge> determiningFlowEdges;
        Set<String> certificate;
        for (int teamIdx = 0; teamIdx < pTeamsCount; teamIdx++) {
            if (eliminated[teamIdx]) {
                continue;
            }
            network = new FlowNetwork(flowNetworkVerticesCount);
            determiningFlowEdges = new LinkedList<>();
            for (int opponentTeamIdx = 0; opponentTeamIdx < pTeamsCount; opponentTeamIdx++) {
                FlowEdge determining;
                if (opponentTeamIdx == teamIdx) {
                    continue;
                }
                int bestCaseDifferenceOfWins = wins(pTeams[teamIdx])
                        + remaining(pTeams[teamIdx])
                        - wins(pTeams[opponentTeamIdx]);
                network.addEdge(
                        new FlowEdge(
                                teamToVertexIdx[opponentTeamIdx],
                                flowNetworkVerticesCount - 1,
                                bestCaseDifferenceOfWins
                        )); // edge to artificial sink
                for (int opponentTeamIdx2 = opponentTeamIdx + 1;
                     opponentTeamIdx2 < pTeamsCount;
                     opponentTeamIdx2++
                ) {
                    if (opponentTeamIdx2 == teamIdx) {
                        continue;
                    }
                    int idxOfMatch = gamesWithToVertexIdx[opponentTeamIdx][opponentTeamIdx2];
                    determining = new FlowEdge(
                            0,
                            idxOfMatch,
                            against(
                                    pTeams[opponentTeamIdx],
                                    pTeams[opponentTeamIdx2]
                            )
                    );
                    network.addEdge(determining); // from source to match
                    determiningFlowEdges.add(determining);
                    network.addEdge(
                            new FlowEdge(
                                    idxOfMatch,
                                    teamToVertexIdx[opponentTeamIdx],
                                    Double.POSITIVE_INFINITY
                            )
                    ); // to first command
                    network.addEdge(
                            new FlowEdge(
                                    idxOfMatch,
                                    teamToVertexIdx[opponentTeamIdx2],
                                    Double.POSITIVE_INFINITY
                            )
                    ); // to second command
                }
            }
            FordFulkerson ff = new FordFulkerson(network, 0, flowNetworkVerticesCount - 1);
            for (FlowEdge flowEdge : determiningFlowEdges) {
                if (flowEdge.flow() == flowEdge.capacity()) {
                    continue;
                }
                /*
                  Non trivial elimination: even if team win all other games,
                  there are a subset of teams, at least one of which wins more
                  games in every possible win distribution.
                 */
                eliminated[teamIdx] = true;
                certificate = new HashSet<>();
                for (int opponentTeamIdx = 0; opponentTeamIdx < pTeamsCount; opponentTeamIdx++) {
                    if (ff.inCut(teamToVertexIdx[opponentTeamIdx])) {
                        certificate.add(pTeams[opponentTeamIdx]);
                    }
                }
                pCertificateOfElimination.put(pTeams[teamIdx], certificate);
                break;
            }
        }
    }

    private void trivialElimination() {
        int currentTeamMaxPossibleNumberOfWins;
        for (int i = 0; i < pTeamsCount; i++) {
            currentTeamMaxPossibleNumberOfWins = wins(pTeams[i]) + remaining(pTeams[i]);
            for (int j = 0; j < pTeamsCount; j++) {
                if (wins(pTeams[j]) > currentTeamMaxPossibleNumberOfWins) {
                    eliminated[i] = true;
                    Set<String> certificate = new HashSet<>();
                    certificate.add(pTeams[j]);
                    pCertificateOfElimination.put(pTeams[i], certificate);
                    break;
                }
            }
        }
    }

    public int numberOfTeams() {
        return pTeamsCount;
    }

    public Iterable<String> teams() {
        return Arrays.asList(pTeams);
    }

    private int getTeamIndex(String team) {
        Integer teamIndex = teamToIdx.get(team);
        if (null == teamIndex) throw new IllegalArgumentException();
        return teamIndex;
    }

    public int wins(String team) {
        return pStats[getTeamIndex(team)][WINS_IDX];
    }

    public int losses(String team) {
        return pStats[getTeamIndex(team)][LOSES_IDX];
    }

    public int remaining(String team) {
        return pStats[getTeamIndex(team)][REMAINING_IDX];
    }

    public int against(String team1, String team2) {
        return pStats[getTeamIndex(team1)][getTeamIndex(team2) + IDX_OF_ZERO_COMMAND_IN_STATS];
    }

    public boolean isEliminated(String team) {
        return eliminated[getTeamIndex(team)];
    }

    public Iterable<String> certificateOfElimination(String team) {
        getTeamIndex(team); // side effect of checking team exists
        return pCertificateOfElimination.get(team);
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
