/**
 * @ngdoc controller
 * @name DatepickerCtrl
 * 
 * @description A controller that holds properties for a datepicker.
 */
app.controller('DatepickerCtrl',
				function($scope) {
					$scope.today = function() {
						$scope.dt = new Date();
					};
					$scope.today();

					$scope.clear = function() {
						$scope.dt = null;
					};

					// Disable weekend selection
					$scope.disabled = function(date, mode) {
						return (mode === 'day' && (date.getDay() === 0 || date
								.getDay() === 6));
					};

					$scope.toggleMin = function() {
						$scope.minDate = ($scope.minDate) ? null : new Date();
					};
					$scope.toggleMin();

					$scope.open = function($event) {
						$event.preventDefault();
						$event.stopPropagation();
						$scope.opened = true;
					};

					$scope.dateOptions = {
						'year-format' : "'yy'",
						'starting-day' : 1
					};

					$scope.formats = [ 'dd-MMMM-yyyy', 'yyyy/MM/dd',
							'shortDate' ,'yyyy-MM-dd HH:mm:ss'];
					$scope.format = $scope.formats[0];
				});
