/**
 * List companies
 */
app.controller('CompanyListCtrl', function($scope, $http) {
	$scope.init = function(){
		
		//company list
		$scope.companies = companies || [];//companies global variable set from html page
	}
	
});
