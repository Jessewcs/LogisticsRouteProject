package com.logistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class DijkstraAdjusted {
  public static void dijkstra(Graph g, String startLabel) {
    Vertex startVertex = g.getVertex(startLabel);
    if (startVertex == null) {
      return;
    }

    PriorityQueue<Vertex> priorityQueue = new PriorityQueue<>(new Vertex.VertexComparator());
    startVertex.distance = 0;
    priorityQueue.add(startVertex);

    while (!priorityQueue.isEmpty()) {
      Vertex current = priorityQueue.poll();

      for (Edge each : current.getEdges()) {
        Vertex neighbor = each.getEnd();
        double updatedD = current.distance + each.getCost();
        if (updatedD < neighbor.distance) {
          neighbor.distance = updatedD;
          neighbor.prev = current;
          priorityQueue.add(neighbor);
        }
      }

    }


  }

  public static double getCheapestCost(Vertex vertex) {
    return vertex.distance;
  }

  public static List<Vertex> getShortestPath(Vertex destination) {
    List<Vertex> path = new ArrayList<>();
    for (Vertex v = destination; v!= null; v = v.prev) {
      path.add(v);
    }
    Collections.reverse(path);
    return path;
  }
}



