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
        '/applications/js/view/configuration/Configuration.view.js',
        '/applications/js/model/configuration/Configuration.js',
        '/applications/js/view/configuration/service/Service.layout.js'
    ], function(Marionette, _, ConfigurationView, ConfigurationModel, ServiceView){
        "use strict";

        var FeatureController = Marionette.Controller.extend({

            initialize: function(options){
                this.region = options.region;
            },

            show: function(appName){
                var self = this;
                self.appName = appName;
                var features = new ConfigurationModel.Response({
                    appName: appName
                });
                features.fetch({
                    success: function() {
                        var servicePage = new ServiceView({model: features});
                        self.region.show(servicePage);
                    }
                });
            }
        });

        return FeatureController;

    }
);