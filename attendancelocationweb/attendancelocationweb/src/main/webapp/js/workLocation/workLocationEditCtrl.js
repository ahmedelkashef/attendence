/**
 * Edit WorkLocation
 */
app.controller('WorkLocationEditCtrl', function($scope, $http) {
	
	$scope.init = function(){
		
		//work location list
		$scope.workLocationPage = workLocationPage || {};//workLocationPage global variable set from html page
		google.maps.event.addDomListener(window, 'load', intializeMap);
		//intializeMap();
	}
	

	function intializeMap() {
		var center = new google.maps.LatLng($scope.workLocationPage.workLocation.latitude, $scope.workLocationPage.workLocation.longitude);
		var mapOpt = {
			center : center,
			zoom : 5,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		};
		//alert(document.getElementById("googleMap"));
		parent.map = new google.maps.Map(document.getElementById("googleMap"),
				mapOpt);
		placeMarker({
			longitude:center.lng(),
			latitude:center.lat(),
			
		});
		
		google.maps.event.addListener(parent.map, 'click', function(event) {
		    updateMarker(event);
		  });
		

	};
	
	function updateMarker(event){
		$scope.$apply(function () {
			 //console.log("Longitude: " + event.latLng.lng() + "\nLatitude: " + event.latLng.lat());
			 $scope.workLocationPage.workLocation.longitude = event.latLng.lng();
			 $scope.workLocationPage.workLocation.latitude = event.latLng.lat();
			 parent.marker.setPosition(event.latLng);
			 parent.infowindow.setContent(getInfowindowContent(event.latLng));
			 //infowindow.open(parent.map, marker);
		});
	}
	
	function getInfowindowContent(location) {
		return "<b>Longitude:</b> " + location.lng() + "<br/> <b>Latitude:</b> " + location.lat()
	}
	 /**
     * add marker to map
     */
    function placeMarker(location) {
    	  var marker = new google.maps.Marker({
    	    position: new google.maps.LatLng(location.latitude, location.longitude),
    	    map: parent.map,
    	    animation:google.maps.Animation.DROP,
    	    draggable:true
    	  });
    	  
    	  parent.marker = marker;
    	 
    	  var infowindow = new google.maps.InfoWindow({
    	    content: getInfowindowContent(parent.marker.position)
    	  });
    	  
    	  parent.infowindow = infowindow;

		 infowindow.open(parent.map, marker);
		 
		 marker.addListener('drag', function(event) {
			 updateMarker(event);
 		});
		 
		 marker.addListener('click', function(event) {
			 $scope.$apply(function () {
			 
			 infowindow.open(parent.map, marker);
			 });
 		});
		 
		 infowindow.open(parent.map, marker);

    	}
    
    $scope.checkWifiIds = function(){
    	return /^(([^,]+,[^,]+)+|([^,]*))$/.test($scope.workLocationPage.workLocation.wifiIDs);
    }

	$scope.save = function(){
		
		if(!$scope.checkWifiIds())
			return;
		
		$scope.loading = true;
        
        var parameter = JSON.stringify($scope.workLocationPage.workLocation);
        var url = "/a/workLocation/save";
        $http.post(url, parameter).
        success(function(data, status, headers, config) {
            // this callback will be called asynchronously
            // when the response is available
        	$scope.loading = false;

            //console.log(data);
        	if(data.error){
        		alert(data.error.message);
        	}else{
        		alert('Location are saved')
        	}
          }).
          error(function(data, status, headers, config) {
        	  $scope.loading = false;
        	  alert('fail to save data');
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        	  //console.log(data);
        	  //console.log(status);
          });

		
		
	};
	
});
