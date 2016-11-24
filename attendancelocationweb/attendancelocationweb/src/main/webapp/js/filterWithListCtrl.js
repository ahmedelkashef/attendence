/**
 * filter data according to requested filters
 */
app.controller('FilterWithListCtrl', function($scope, $http, $uibModal) {
	var parent = this;
	$scope.init = function(){
		//$scope.queryAttendances();
		
		//location list
		$scope.search = {date: new Date(), email:""};
		$scope.locations = [];
		$scope.queryWithFilters();
	}
	
	$scope.animationsEnabled = true;
	
	//open modal with parameter data
	$scope.open = function (size, location) {

	    var modalInstance = $uibModal.open({
	      animation: $scope.animationsEnabled,
	      templateUrl: '/html/viewImageModal.html',
	      controller: 'ModalInstanceViewImageCtrl',
	      size: size,
	      resolve: {
	        bundle: function () {
	          return {url:location.imageUrl, email:location.email};
	        }
	      }
	    });
	};
	
	/**
     * Holds the status if the query is being executed.
     * @type {boolean}
     */
    $scope.submitted = false;
    
    /**
     * Holds the filters that will be applied .
     * @type {Array}
     */
    $scope.filters = [
    ];
    
    //date field name
    $scope.dataField = 'date';

    $scope.filtereableFields = [
        
        {enumValue: 'email', displayName: 'Email'},
        {enumValue: $scope.dataField, displayName: 'Date'},
        {enumValue: 'status', displayName: 'Status'}
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