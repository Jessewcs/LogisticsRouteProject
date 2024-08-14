package com.logistics;


import java.util.ArrayList;
import java.util.List;

public class Vertex {
  private String label;
  private List<Edge> edges;
  private double latitude;
  private double longitude;
  public double distance;
  public Vertex prev;


  public Vertex(String label, double latitude, double longitude) {
    this.label = label;
    this.edges = new ArrayList<>();
    this.latitude = latitude;
    this.longitude = longitude;
    this.distance = Double.POSITIVE_INFINITY;
    this.prev = null;
  }

  public void addEdge(Edge e) {
    this.edges.add(e);
  }

  public String getLabel() {
    return this.label;
  }

  public List<Edge> getEdges() {
    return this.edges;
  }

  public double getDistance() {
    return this.distance;
  }

  public Vertex getPrev() {
    return this.prev;
  }
  public double getLatitude() {
    return this.latitude;
  }

  public double getLongitude() {
    return this.longitude;
  }

  public String getId() {
    return this.label;
  }

  public static class VertexComparator implements java.util.Comparator<Vertex> {
    @Override
    public int compare(Vertex v1, Vertex v2) {
      return Double.compare(v1.distance, v2.distance);
    }
  }
}

