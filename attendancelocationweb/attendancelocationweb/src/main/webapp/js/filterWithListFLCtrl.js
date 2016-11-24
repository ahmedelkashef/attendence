/**
 * attendace first in last out 
 */
app.controller('FilterWithListFLCtrl', function($scope, $http, $uibModal) {
	
	var statusStyle = {
			"Incomplete":{"font-weight": "bold","color":"orange"},
			"Absent":{"font-weight": "bold","color":"red"},
			"Present":{"font-weight": "bold","color":"green"}
	}
	$scope.search = {};
	
	$scope.TabGroup = "group";
	$scope.TabEmployee = "employee";
	$scope.init = function(){
		
		$scope.page = page || {};//page global variable set from html page
		$scope.search = {startDate: new Date($scope.page.search.startDate),
				endDate: new Date($scope.page.search.endDate),
				selectedGroup:$scope.page.groups[$scope.page.search.groupIndex],
				activeTab:$scope.page.search.activeTab,
				email:$scope.page.search.email};
		$scope.loading = false;
	}
	
	$scope.animationsEnabled = true;
	
	//open modal with parameter data
	$scope.open = function (size, name, url) {

	    var modalInstance = $uibModal.open({
	      animation: $scope.animationsEnabled,
	      templateUrl: '/html/viewImageModal.html',
	      controller: 'ModalInstanceViewImageCtrl',
	      size: size,
	      resolve: {
	        bundle: function () {
	          return {url:url, email:name};
	        }
	      }
	    });
	};
	
	$scope.getStatusStyle = function(status){
		return statusStyle[status];
	};
	
	$scope.apply = function(){
		$scope.loading = true;
		location.search = "?startDate={0}&endDate={1}&groupIds={2}&defaulteRequest={3}&activeTab={4}&email={5}"
			.format(
					$scope.search.startDate.getTime(),
					$scope.search.endDate.getTime(),
					$scope.search.selectedGroup.id,
					false,
					$scope.search.activeTab,
					$scope.email);
	};
	
	$scope.changeTab = function(tab){
		$scope.search.activeTab = tab;
	};
	
    /**
     * Ajax call to back end api
     */
    $scope.queryWithFilters = function () {
    	$scope.locations = [];
    	//json object to be sent
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
        //console.log(sendFilters);
        $scope.loading = true;
        
        var parameter = JSON.stringify(sendFilters);
        var url = "/a/viewData/queryAttendanceLogs";
        $http.post(url, parameter).
        success(function(data, status, headers, config) {
            // this callback will be called asynchronously
            // when the response is available
        	$scope.loading = false;
            //console.log(data);
            $scope.submitted = true;
            $scope.locations = data.items;
          }).
          error(function(data, status, headers, config) {
        	  $scope.loading = false;
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        	  //console.log(data);
        	  //console.log(status);
          });
        
       
    }

	
});

/**
 * modal controller that view some location details
 */
app.controller('ModalInstanceViewImageCtrl', function ($scope, $uibModalInstance, bundle) {

	$scope.bundle = bundle;
	  $scope.ok = function () {
		 $uibModalInstance.dismiss('cancel');
	  };

	});