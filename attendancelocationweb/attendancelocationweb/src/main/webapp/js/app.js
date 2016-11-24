/**
 * angular application
 */
var app = angular.module('loacationAttendance', ['ngAnimate', 'ui.bootstrap']);

app.controller('NavBarCtrl', function ($scope, $uibModal) {
	
	$scope.openLogoModal = function () {
		
		
	    var modalInstance = $uibModal.open({
	      animation: $scope.animationsEnabled,
	      templateUrl: '/templates/company/uploadLogoModal.html',
	      controller: 'ChangeLogeModalCtrl',
	      size: 'sm'

	    });
	};
	
	
});

app.controller('ChangeLogeModalCtrl', function ($scope, $http,$uibModalInstance) {
	
	$scope.uploadUrl = "";
	
	$scope.init = function(){
		var parameter = "";
        var url = "/a/company/upload/createUploadUrl";
        $http.post(url, parameter).
        success(function(data, status, headers, config) {
            // this callback will be called asynchronously
            // when the response is available
        	$scope.loading = false;
            //console.log(data);
            $scope.submitted = true;
            $scope.uploadUrl = data.uploadUrl;
          }).
          error(function(data, status, headers, config) {
        	  $scope.loading = false;
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        	  //console.log(data);
        	  //console.log(status);
          });
	}
	
	  $scope.ok = function () {
		  //alert(JSON.stringify(grade));
	    $uibModalInstance.close(true);
	  };

	  $scope.cancel = function () {
		  $uibModalInstance.dismiss('cancel');
	  };
	});

//First, checks if it isn't implemented yet.
if (!String.prototype.format) {
  String.prototype.format = function() {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function(match, number) { 
      return typeof args[number] != 'undefined'
        ? args[number]
        : match
      ;
    });
  };
}
