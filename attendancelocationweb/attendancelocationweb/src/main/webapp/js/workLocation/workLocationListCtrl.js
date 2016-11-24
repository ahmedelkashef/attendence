/**
 * List workLocations
 */
app.controller('WorkLocationListCtrl', function($scope) {
	var parent = this;
	$scope.init = function(){
		//workLocations list
		$scope.workLocations = workLocations || [];//workLocations global variable set from html page
	};
	
});
