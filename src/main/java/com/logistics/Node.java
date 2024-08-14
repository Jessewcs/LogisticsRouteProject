package com.logistics;


public class Node implements Comparable<Node> {
  private String id; // Airport or Port identifier
  private double travelTime; // Time from the source node

  public Node(String id, double travelTime) {
    this.id = id;
    this.travelTime = travelTime;
  }

  public String getId() {
    return id;
  }

  public double getTravelTime() {
    return travelTime;
  }

  public void setTravelTime(double travelTime) {
    this.travelTime = travelTime;
  }

  @Override
  public int compareTo(Node other) {
    return Double.compare(this.travelTime, other.travelTime);
  }
}