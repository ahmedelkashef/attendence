/**
 * filter data according to requested filters
 */
app.controller('FilterWithMapHistoryCtrl', function($scope, $http) {
	var parent = this;
	$scope.init = function(){
		$scope.search = {date: new Date(), email:""};
		intializeMap();
		//$scope.queryLocations();
	}
	
	/**
     * Holds the status if the query is being executed.
     * @type {boolean}
     */
    $scope.submitted = false;
    
    /**
     * Holds the filters that will be applied.
     * @type {Array}
     */
    $scope.filters = [
    ];
    
    //date field name
    $scope.dataField = 'date';

    $scope.filtereableFields = [
        
        {enumValue: 'email', displayName: 'Email'},
        {enumValue: $scope.dataField, displayName: 'Date'}
    ]

    /**
     * Possible operators.
     *
     * @type {{displayName: string, enumValue: string}[]}
     */
    $scope.operators = [
        {displayName: 'equals', enumValue: '='},
        {displayName: 'after', enumValue: '>='},
        {displayName: 'before', enumValue: '<='}
    ];
    
    /**
     * Adds a filter and set the default value.
     */
    $scope.addFilter = function () {
        $scope.filters.push({
            field: $scope.filtereableFields[0],
            operator: $scope.operators[0],
            value: ''
        })
    };

    /**
     * Clears all filters.
     */
    $scope.clearFilters = function () {
        $scope.filters = [];
        deleteMarkers();
    };

    /**
     * Removes the filter specified by the index from $scope.filters.
     *
     * @param index
     */
    $scope.removeFilter = function (index) {
        if ($scope.filters[index]) {
            $scope.filters.splice(index, 1);
        }
    };
    
    /**
     * field name changed handler
     */
    $scope.fieldChanged = function(filter){
    	if(filter.field.enumValue == $scope.dataField)
    		filter.value = new Date();
    	else
    		filter.value = '';
    }
    
    /**
     * Ajax call to back end api
     */
    $scope.queryWithFilters = function () {
    	deleteMarkers();
        var sendFilters = {
            filters: []
        }
        
        if($scope.search.email && $scope.search.email.trim() != ""){
          	 sendFilters.filters.push({
                   field: "email",
                   operator: "=",
                   value: $scope.search.email
                 });
          }
         
          
          var startDate = new Date();
          var endDate = new Date();
          
          startDate.setTime($scope.search.date.getTime());
          startDate.setHours(0);
          startDate.setMinutes(0);
          startDate.setSeconds(0);
          startDate.setMilliseconds(0);
          
          endDate.setTime($scope.search.date.getTime());
          endDate.setHours(23);
          endDate.setMinutes(59);
          endDate.setSeconds(59);
          endDate.setMilliseconds(999);
          
          sendFilters.filters.push({
              field: "date",
              operator: ">=",
              dateLong: startDate.getTime()
            });
          
          sendFilters.filters.push({
              field: "date",
              operator: "<=",
              dateLong: endDate.getTime()
            });
          
//        for (var i = 0; i < $scope.filters.length; i++) {
//            var filter = $scope.filters[i];
//            if (filter.field && filter.operator && filter.value) {
//                sendFilters.filters.push({
//                    field: filter.field.enumValue,
//                    operator: filter.operator.enumValue,
//                    value: filter.value,
//                    dateLong: (filter.field.enumValue && filter.field.enumValue == $scope.dataField ? filter.value.getTime():-1)
//                });
//            }
//        }
//        console.log(sendFilters);
        $scope.loading = true;
        
        var parameter = JSON.stringify(sendFilters);
        var url = "/a/viewData/queryTrackingHistory";
        $http.post(url, parameter).
        success(function(data, status, headers, config) {
        	
            // this callback will be called asynchronously
            // when the response is available
        	$scope.loading = false;
            //console.log(data);
            $scope.submitted = true;
            placeMarkers(data.items);
          }).
          error(function(data, status, headers, config) {
        	  $scope.loading = false;
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        	  //console.log(data);
        	  //console.log(status);
          });
        
       
    }
    
    /**
     * show all location on map
     */
    function placeMarkers(locations) {
    	angular.forEach(locations, function (location) {
	      
    		placeMarker(location);
    		
//    		var marker = new google.maps.Marker({
//        	    position: new google.maps.LatLng(location.latitude, location.longitude),
//        	    map: parent.map,
//        	    animation:google.maps.Animation.DROP,
//        	    draggable:true
//        	  });
//        	 
//        	  var infowindow = new google.maps.InfoWindow({
//        	    content: getInfoWindowContent(location)
//        	  });
//        	  
//        	  marker.addListener('click', function() {
//        		  infowindow.open(parent.map, marker);
//      			});
//        	  
        	  
    	});
    	
    	parent.path = new google.maps.Polyline({
    		  path:parent.markers,
    		  strokeColor:"#0000FF",
    		  strokeOpacity:0.6,
    		  strokeWeight:4
    		  });

    	parent.path.setMap(parent.map);
    	
    	if(locations.length > 0){
    		var location = locations[locations.length -1];
    		parent.lastLocation = new google.maps.Marker({
        	    position: new google.maps.LatLng(location.latitude, location.longitude),
        	    map: parent.map,
        	    animation:google.maps.Animation.DROP
        	  });
        	 
        	  var infowindow = new google.maps.InfoWindow({
        	    content: getInfoWindowContent(location)
        	  });
        	  
        	  infowindow.open(parent.map, parent.lastLocation);
    	}
    	
 
//    	console.log(parent.markers);
//    	console.log(parent.markers[0].lat());
//    	console.log(parent.markers[0].lng());
		//parent.map.setCenter(x);
//		parent.map.fitBounds(parent.bounds);
  	}
    /**
     * add marker to map
     */
    function placeMarker(location) {
    	  var position = new google.maps.LatLng(location.latitude, location.longitude);
    	 
    		parent.markers.push(position);
    		parent.bounds.extend(position);
    		parent.map.setCenter(parent.bounds.getCenter());
    		parent.map.fitBounds(parent.bounds);

    	}
    

    // Deletes all markers in the array by removing references to them.
    function deleteMarkers() {
      parent.markers = [];
      parent.path.setMap(null);
      parent.bounds = new google.maps.LatLngBounds();
      
      if(parent.lastLocation)
    	  parent.lastLocation.setMap(null);
    }
    
    function intializeMap()
    {
    var mapOpt = {
      center:new google.maps.LatLng(51.508742,-0.120850),
      zoom:5,
      mapTypeId:google.maps.MapTypeId.ROADMAP
      };
    parent.map = new google.maps.Map(document.getElementById("googleMap"),mapOpt);
    parent.bounds = new google.maps.LatLngBounds();
    parent.markers = [];
    
    parent.path = new google.maps.Polyline({
		  path:[],
		  strokeColor:"#0000FF",
		  strokeOpacity:0.8,
		  strokeWeight:2
		  });
   
    }
    
    /**
     * build InfoWindow content from location parameter
     * @param location
     * @returns {String}
     */
    getInfoWindowContent = function(location){
    	var d = new Date(location.date);
    	
    	var contentString  = '<div id="contentInf">' +
		'<h4>' + location.email + '</h4>' +
		'<div>' +
			'<div>' +
				'<b>Date:</b> <span>' + d.toLocaleDateString() + ' ' + d.toLocaleTimeString() + '</span><br/>' +
				'<b>Providers:</b> <span>' + location.providers + '</span><br/>' +
			'</div>' +
		'</div>' +
	'</div>';
    	
    	return contentString;
    	
    }

	
});