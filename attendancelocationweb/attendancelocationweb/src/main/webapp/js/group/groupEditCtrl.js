/**
 * Edit Group
 */
app.controller('GroupEditCtrl', function($scope, $http) {
	
	$scope.intervals =  [1, 5, 10, 15, 25, 30, 60, 90];
	$scope.hours = [];//[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23];
	$scope.timeZones = [];//[-1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -11, -12, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
	$scope.workDays = [
	                   {name:'Sunday', included:false, order:1},
	                   {name:'Monday', included:false, order:2},
	                   {name:'Tuesday', included:false, order:3},
	                   {name:'Wednesday', included:false, order:4},
	                   {name:'Thursday', included:false, order:5},
	                   {name:'Friday', included:false, order:6},
	                   {name:'Saturday', included:false, order:7}
	                   
	                   ];
	$scope.workShift = {};
	$scope.init = function(){
		
		//group list
		$scope.groupPage = groupPage || {};//group global variable set from html page
		
		angular.forEach($scope.groupPage.members, function (member) {
		      
			member.memberOrigin = true;
    	});
		
		angular.forEach($scope.groupPage.managers, function (manager) {
		      
			manager.managerOrigin = true;
    	});
		

		$scope.groupPage.groupSettings.trackingInterval  /= 60;// sec to min
		$scope.groupPage.groupSettings.trackingInterval = Math.floor($scope.groupPage.groupSettings.trackingInterval);
		
		angular.forEach($scope.groupPage.workLocations, function (location) {
		      
			if(location.id == $scope.groupPage.groupSettings.workLocationId)
				$scope.groupPage.workLocation = location;
    	});
		
		
		//init Workdays
		
		angular.forEach($scope.workDays, function (workDay) {
			
			if($scope.groupPage.groupSettings.workShift.workDays.includes(workDay.order))
				workDay.included = true;
    	});
		
		//init time lists
		generateTimeLists();
	}
	
	generateTimeLists = function(){
		
		$scope.hours = [];
		for(var i = 0;i <= 23;i++)
			$scope.hours.push((i <= 9? '0'+i : i));
		
		$scope.minutes = [];
		for(var i = 0;i <= 59;i++)
			$scope.minutes.push((i <= 9? '0'+i : i));
		
		$scope.timeZones = [];
		for(var i = -12;i <= 12;i++)
			if(i < 0)
				$scope.timeZones.push((Math.abs(i) <= 9? '-0'+Math.abs(i) : i));
			else if(i > 0)
				$scope.timeZones.push((i <= 9? '+0'+i : i));
			else//0
				$scope.timeZones.push('0'+i);
		
		var startTime = $scope.groupPage.groupSettings.workShift.startTime;
		var hours = Math.floor(startTime / 60);
		var minutes = startTime % 60;
		$scope.workShift.startHour = (hours <=9 ? '0'+hours: hours);
		$scope.workShift.startMinute = (minutes <=9 ? '0'+minutes: minutes);
		
		var endTime = $scope.groupPage.groupSettings.workShift.endTime;
		hours = Math.floor(endTime / 60);
		minutes = endTime % 60;
		$scope.workShift.endHour = (hours <=9 ? '0'+hours: hours);
		$scope.workShift.endMinute = (minutes <=9 ? '0'+minutes: minutes);
		
		var timeZone = $scope.groupPage.groupSettings.workShift.timeZone;
		if(timeZone < 0)
			$scope.workShift.timeZone = (Math.abs(timeZone) <= 9? '-0'+Math.abs(timeZone) : timeZone);
		else if(timeZone > 0)
			$scope.workShift.timeZone = (timeZone <= 9? '+0'+timeZone : timeZone);
		else//0
			$scope.workShift.timeZone = '0'+timeZone;
		
	}
	
	$scope.addToGroup = function(employee){
		employee.memSelected = true;
		if(!employee.memRemove)
			$scope.groupPage.members.push(employee);
		
		employee.memRemove = false;
		employee.groups.push($scope.groupPage.groupSettings.id);
	};
	
	$scope.removeFromGroup = function(member){
		member.memRemove = true;
		if(!member.memSelected)
			$scope.groupPage.employees.push(member);
		
		member.memSelected = false;
		member.groups = [];
		
	};
	
	$scope.addToManagers = function(employee){
		employee.managSelected = true;
		if(!employee.managRemove)
			$scope.groupPage.managers.push(employee);
		
		employee.managRemove = false;
	};
	
	$scope.removeFromManagers = function(manager){
		manager.managRemove = true;
		if(!manager.managSelected)
			$scope.groupPage.employees.push(manager);
		
		manager.managSelected = false;
		
	};
	
	$scope.save = function(){
//		if(!$scope.validShift())
//			return;
		$scope.groupPage.groupSettings.managers = [];
		$scope.groupPage.groupSettings.members = [];
		
		angular.forEach($scope.groupPage.members, function (member) {
		      
			if(member.memSelected != false){
				$scope.groupPage.groupSettings.members.push(member.id);
			}
    	});
		
		angular.forEach($scope.groupPage.managers, function (manager) {
		      
			if(manager.managSelected != false){
				$scope.groupPage.groupSettings.managers.push(manager.id);
			}
    	});
		
		$scope.groupPage.groupSettings.trackingInterval  *= 60;
		
		if($scope.groupPage.workLocation){
			$scope.groupPage.groupSettings.workLocationId = $scope.groupPage.workLocation.id;
		}
		
		//init work location
		$scope.groupPage.groupSettings.workShift.workDays = "";
		angular.forEach($scope.workDays, function (workDay) {
			
			if(workDay.included)
				$scope.groupPage.groupSettings.workShift.workDays += workDay.order;
    	});
		
		$scope.groupPage.groupSettings.workShift.startTime = 
			parseInt($scope.workShift.startHour)*60 + parseInt($scope.workShift.startMinute);
		$scope.groupPage.groupSettings.workShift.endTime = 
			parseInt($scope.workShift.endHour)*60 + parseInt($scope.workShift.endMinute);
		$scope.groupPage.groupSettings.workShift.timeZone = parseInt($scope.workShift.timeZone);
		
		$scope.loading = true;
        
        var parameter = JSON.stringify($scope.groupPage.groupSettings);
        var url = "/a/group/save";
        $http.post(url, parameter).
        success(function(data, status, headers, config) {
            // this callback will be called asynchronously
            // when the response is available
        	$scope.loading = false;

    		$scope.groupPage.groupSettings.trackingInterval  /= 60;
    		$scope.groupPage.groupSettings.trackingInterval = Math.floor($scope.groupPage.groupSettings.trackingInterval);
            //console.log(data);
        	if(data.error){
        		alert(data.error.message);
        	}else{
        		alert('Settings are saved')
        	}
          }).
          error(function(data, status, headers, config) {
        	  $scope.loading = false;
        	  alert('fail to save data')

      		$scope.groupPage.groupSettings.trackingInterval  /= 60;
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        	  //console.log(data);
        	  //console.log(status);
          });

		
		
	};
	
	$scope.validShift = function(){
		return $scope.groupPage.groupSettings.workShift.startHour < $scope.groupPage.groupSettings.workShift.endHour;
	};
	
	
});
