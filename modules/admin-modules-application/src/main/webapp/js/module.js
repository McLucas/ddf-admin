/**
 * Copyright (c) Codice Foundation
 *
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 *
 **/
/* jshint unused: false */
/*global define*/
define([
    'js/application',
    'require',
    '/applications/templateConfig.js'
    ],function(Application, require) {


    Application.App.module('Applications', function(ApplicationModule, App, Backbone, Marionette, $, _) {

        require([
                '/applications/js/view/ApplicationGrid.view.js',
                '/applications/js/model/ApplicationsLayout.js',
               '/applications/js/view/features/Features.view.js',
               '/applications/js/model/features/Feature.js'

            ], function(ApplicationView, ApplicationModel, FeaturesView, FeatureModel) {
            var appPage = new ApplicationView({modelClass: ApplicationModel, enableApplicationRemoval: true});
            var featureView = new FeaturesView();
            //var testCollection = new FeatureModel.Collection();
            //var featuresJson = testCollection.fetch();
            //$('body').append(featureView.render().$el);

            // Define a controller to run this module
            // --------------------------------------

            var Controller = Marionette.Controller.extend({

                initialize: function(options){
                    this.region = options.region;
                },

                show: function(){
                    this.region.show(appPage);
                    //this.region.show(featureView);
                }

            });


            // Initialize this module when the app starts
            // ------------------------------------------

            ApplicationModule.addInitializer(function(){
                ApplicationModule.contentController = new Controller({
                    region: App.applications
                });
                ApplicationModule.contentController.show();
            });
        });
    });
});
