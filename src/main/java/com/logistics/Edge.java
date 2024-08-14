package com.logistics;


public class Edge {
  private Vertex starter;
  private Vertex end;
  private double weight;
  private double cost;
  private TravelType type;

  public Edge(Vertex starter, Vertex end, double weight, double cost, TravelType type) {
    this.starter = starter;
    this.end = end;
    this.weight = weight; //distance
    this.cost = cost; // how to put cost into the code ?
    this.type = type;
  }

  public Vertex getStarter() {
    return this.starter;
  }

  public Vertex getEnd() {
    return this.end;
  }

  public double getWeight() {
    return this.weight;
  }

  public double getCost() {
    return this.cost;
  }

  public TravelType getType() {
    return this.type;
  }
}
