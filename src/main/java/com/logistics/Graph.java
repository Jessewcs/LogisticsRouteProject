package com.logistics;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Graph {
  private Map<String, Vertex> vertices;

  public Graph() {
    this.vertices = new HashMap<>();
  }

  public void addVertex(String label, double latitude, double longitude) {
    vertices.put(label, new Vertex(label, latitude, longitude));
  }

  public Vertex getVertex(String label) {
    return vertices.get(label);
  }

  public void addEdge(String startLabel, String endLabel, double weight, double cost, TravelType type) {
    Vertex start = getVertex(startLabel);
    Vertex end = getVertex(endLabel);
    if (start != null && end != null) {
      start.addEdge(new Edge(start, end, weight, cost, type));
    }
  }

  public Collection<Vertex> getAllVertices() {
    return vertices.values();
  }
}
