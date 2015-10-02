// Generated by CoffeeScript 1.7.1
(function() {
  var mosUnsavedChanges,
    __slice = [].slice;

  mosUnsavedChanges = angular.module('mosSvcs.unsavedChanges', []);

  mosUnsavedChanges.service('unsavedChanges', [
    '$rootScope', '$window', '$route', function($rootScope, $window) {
      var MESSAGE, fnLocationListener, oListenableCollection;
      MESSAGE = "Are you sure you don't want to save changes?";
      oListenableCollection = {};
      fnLocationListener = null;
      this.fnAttachListener = function($scope, sSlug, mListenableObject) {
        oListenableCollection[sSlug] = {};
        oListenableCollection[sSlug].oPristine = angular.copy(mListenableObject);
        oListenableCollection[sSlug].oCurrent = mListenableObject;
        if (fnLocationListener != null) {
          return;
        }
        fnLocationListener = $rootScope.$on('$locationChangeStart', (function(_this) {
          return function(oEvent) {
            var oListenableObject;
            for (sSlug in oListenableCollection) {
              oListenableObject = oListenableCollection[sSlug];
              if (_this.fnHasChanges(sSlug) === true) {
                if (confirm(MESSAGE)) {
                  break;
                }
                return oEvent.preventDefault();
              }
            }
            return _this.fnDetachListeners();
          };
        })(this));
        $window.onbeforeunload = (function(_this) {
          return function(oEvent) {
            var oListenableObject;
            for (sSlug in oListenableCollection) {
              oListenableObject = oListenableCollection[sSlug];
              if (_this.fnHasChanges(sSlug) === true) {
                if (typeof oEvent === "undefined") {
                  oEvent = $window.event;
                }
                if (oEvent != null) {
                  oEvent.returnValue = MESSAGE;
                }
                return MESSAGE;
              }
            }
          };
        })(this);
        return $scope.$on('$destroy', (function(_this) {
          return function() {
            return _this.fnDetachListeners();
          };
        })(this));
      };
      this.fnDetachListener = function(sSlug) {
        if (!angular.isString(sSlug)) {
          return;
        }
        if (oListenableCollection.hasOwnProperty(sSlug)) {
          return delete oListenableCollection[sSlug];
        }
      };
      this.fnDetachListeners = function() {
        oListenableCollection = {};
        if (fnLocationListener != null) {
          fnLocationListener();
        }
        fnLocationListener = null;
        return $window.onbeforeunload = null;
      };
      this.fnReAttachListener = function(sSlug) {
        return oListenableCollection[sSlug].oPristine = angular.copy(oListenableCollection[sSlug].oCurrent);
      };
      this.fnTrigger = function() {
        var aSlugs, bTotalChanges, sSlug, _i, _len;
        aSlugs = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
        bTotalChanges = false;
        for (_i = 0, _len = aSlugs.length; _i < _len; _i++) {
          sSlug = aSlugs[_i];
          bTotalChanges = bTotalChanges || this.fnHasChanges(sSlug);
        }
        return bTotalChanges && !confirm(MESSAGE);
      };
      this.fnHasChanges = function(sSlug) {
        if ((!angular.isString(sSlug)) || !oListenableCollection.hasOwnProperty(sSlug)) {
          return false;
        }
        return !angular.equals(oListenableCollection[sSlug].oPristine, oListenableCollection[sSlug].oCurrent);
      };
    }
  ]);

}).call(this);