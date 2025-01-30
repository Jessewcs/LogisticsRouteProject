# Enhanced Pathfinding for International Logistics and Travel 🌎✈️

## Overview
An innovative pathfinding application that enhances international logistics and travel planning by integrating multi-modal transport options (air, sea, and road) using a modified Dijkstra's algorithm. The project successfully matches Google Maps' accuracy while providing extended functionality for international route planning.

## Team Members
- Karthikeyan Sugavanan
- Prisha Sujin Kumar
- Kavya Veeramony
- Jesse Wojtanowicz

## Project Presentation 🎥
[![Project Demo](https://img.youtube.com/vi/y-aEz7yoAkw/0.jpg)](https://www.youtube.com/watch?v=y-aEz7yoAkw)

## Key Features 🚀
- **Multi-Modal Transport Integration**: Seamlessly combines air, sea, and road transportation options
- **International Route Planning**: End-to-end solution for cross-border logistics and travel
- **Comprehensive Database**: Contains over 6,000 airports and ports across multiple regions
- **Advanced Pathfinding**: Modified Dijkstra's algorithm optimized for international travel
- **Google Maps Integration**: Uses Google Maps API for final leg routing and validation
- **Real-World Accuracy**: Achieves comparable accuracy to Google Maps for route calculations

## Technical Implementation 💻
### Core Components
- Modified Dijkstra's Algorithm for multi-modal pathfinding
- Custom distance calculation using Haversine Formula
- Priority queue-based route optimization
- Dynamic node selection based on transport modes

### Technologies Used
- **Backend**: Java Spring Boot
- **Database**: Comprehensive CSV data storage
- **APIs**: Google Maps Integration
- **Algorithms**: Modified Dijkstra's, Haversine Formula
- **Data Structures**: Priority Queues, Hash Maps

## Database Coverage 🗺️
- USA
- Europe
- Gulf Region
- South Asia

## Installation & Setup 🛠️
1. Clone the repository
```bash
git clone https://github.com/YourUsername/international-logistics-pathfinding.git
```

2. Install dependencies
```bash
# Navigate to project directory
cd international-logistics-pathfinding

# Set up Java environment
# Make sure you have JDK 11 or higher installed
```

3. Configure API keys
```bash
# Add your Google Maps API key in local.properties
MAPS_API_KEY=your_api_key_here
```

4. Run the application
```bash
./gradlew bootRun
```

## Usage Examples 📝
```java
// Example: Finding the optimal route between two international locations
FlightPathFinder.findShortestFlightPath(flights, "BOS", "MAA");

// Example: Finding nearest airport to coordinates
Airport nearestAirport = findNearestAirport(42.3601, -71.0589, airports);
```

## Project Achievements 🏆
- Successfully implemented a modified Dijkstra's algorithm for international route planning
- Created a comprehensive database of transportation nodes
- Achieved accuracy comparable to Google Maps
- Integrated multiple transport modes seamlessly
- Developed efficient data structures for optimal performance

## Future Enhancements 🔮
- Real-time data integration for live traffic and flight information
- Expanded geographical coverage
- Enhanced algorithm optimization for larger datasets
- Separate systems for package delivery and passenger travel
- Integration of pricing and convenience factors

## Limitations 📊
- Real-time data not currently incorporated
- Limited to specific geographical regions
- Performance may degrade with extremely large datasets


## Contact 📧
For any queries regarding this project, please contact any of the team members:
- [Jesse Wojtanowicz](https://github.com/Jessewcs)

---
*This project was developed as part of a course at Northeastern University.*
