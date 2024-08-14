let map;

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: 40.7128, lng: -74.0060 },
        zoom: 7
    });
}

document.getElementById('routeForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const origin = document.getElementById('origin').value;
    const destination = document.getElementById('destination').value;
    const departureTime = document.getElementById('departureTime').value;

    const data = {
        origin: origin,
        destination: destination,
        departureTime: departureTime
    };

    fetch('/api/routes/calculate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(result => {
        displayResult(result);
    })
    .catch(error => {
        console.error('Error:', error);
        document.getElementById('result').innerHTML = 'An error occurred while calculating the route.';
    });
});

function displayResult(result) {
    if (result.error) {
        document.getElementById('result').innerHTML = `Error: ${result.error}`;
        return;
    }

    let resultHtml = `
        <h2>Route Details</h2>
        <p><strong>Origin Airport:</strong> ${result.originAirport}</p>
        <p><strong>Destination Airport:</strong> ${result.destinationAirport}</p>
        <p><strong>Departure Time:</strong> ${result.departureTime}</p>
        <h3>Flight Path:</h3>
        <ul>
    `;

    result.flightPath.forEach(flight => {
        resultHtml += `
            <li>
                ${flight.airline} Flight ${flight.flightID}<br>
                From: ${flight.originAirport} (${flight.originCountry})<br>
                To: ${flight.destinationAirport} (${flight.destinationCountry})<br>
                Flight Time: ${flight.totalFlightTime} minutes<br>
                Departure: ${flight.departureTime}, Arrival: ${flight.arrivalTime}
            </li>
        `;
    });

    resultHtml += '</ul>';

    document.getElementById('result').innerHTML = resultHtml;

    // Update map with route
    updateMap(result);
}

function updateMap(result) {
    const directionsService = new google.maps.DirectionsService();
    const directionsRenderer = new google.maps.DirectionsRenderer();
    directionsRenderer.setMap(map);

    const waypoints = result.flightPath.map(flight => ({
        location: flight.destinationAirport,
        stopover: true
    }));

    const request = {
        origin: result.originAirport,
        destination: result.destinationAirport,
        waypoints: waypoints,
        travelMode: 'FLYING'
    };

    directionsService.route(request, function(result, status) {
        if (status === 'OK') {
            directionsRenderer.setDirections(result);
        }
    });
}

window.onload = initMap;