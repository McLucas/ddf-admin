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
/*global define*/
define([
        'marionette',
        'underscore',
        '/applications/js/view/features/Features.view.js',
        '/applications/js/model/features/Feature.js'
    ], function(Marionette, _, FeaturesView, FeatureModel){
        "use strict";

        var FeatureController = Marionette.Controller.extend({

            initialize: function(options){
                this.region = options.region;
            },

            show: function(appName){
                var view = this;
                var features = new FeatureModel.Collection({
                    type: 'app',
                    appName: appName
                });
                features.fetch({
                    success: function(collection) {
                        var featureView = new FeaturesView({
                            collection: collection
                        });
                        view.region.show(featureView);
                    }
                });
            },

            showAll: function(){
                var view = this;
                var features = new FeatureModel.Collection({
                    type: 'all'
                });
                features.fetch({
                    success: function(collection) {
                        var featureView = new FeaturesView({
                            collection: collection
                        });
                        view.region.show(featureView);
                    }
                });
            }

        });

        return FeatureController;

    }
);