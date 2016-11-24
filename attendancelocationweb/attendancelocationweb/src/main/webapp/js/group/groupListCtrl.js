/**
 * List groups
 */
app.controller('GroupListCtrl', function($scope, $http) {
	$scope.init = function(){
		
		//group list
		$scope.groups = groups || [];//groups global variable set from html page
	}
	
});
