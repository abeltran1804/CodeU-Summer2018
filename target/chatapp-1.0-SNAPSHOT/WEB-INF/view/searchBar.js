//!--------------------------
  // Code to display search box
  // Create the search box and link it to the UI element.
  //document.getElementById("listPlaces").innerHTML = localStorage.length;
  var input = document.getElementById('pac-input');
  var searchBox = new google.maps.places.SearchBox(input);
  var locationDisplay = new google.maps.InfoWindow;
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

  // Bias the SearchBox results towards current map's viewport.
  map.addListener('bounds_changed', function() {
    searchBox.setBounds(map.getBounds());
  });

  markers = [];
  // Listen for the event fired when the user selects a prediction and retrieve
  // more details for that place.
  searchBox.addListener('places_changed', function() {
    var places = searchBox.getPlaces();

    if (places.length == 0) {
      return;
    }

    clearMarkers();

    // For each place, get the icon, name and location.
    var bounds = new google.maps.LatLngBounds();
    clearMarkers();
    places.forEach(function(place) {
      if (!place.geometry) {
        console.log("Returned place contains no geometry");
        return;
      }

      // Create a marker for each place.
      var lookMarker = new google.maps.Marker({
        map: map,
        title: place.name,
        position: place.geometry.location
      });

      markers.push(lookMarker);

      google.maps.event.addListener(lookMarker, 'click', function() {
        // Open an info window when the marker is clicked on, containing the text
        // of the step.
        stepDisplay.setContent('Name: ' + place.name + '<br>Adress: ' + place.formatted_address + '<br>Phone: ' + place.formatted_phone_number + '<br>Rating: ' + place.rating+ '<br>'+ place.geometry.location);
        stepDisplay.open(map, lookMarker);
      });

      if (place.geometry.viewport) {
        // Only geocodes have viewport.
        bounds.union(place.geometry.viewport);
      } else {
        bounds.extend(place.geometry.location);
      }
    });
    map.fitBounds(bounds);
