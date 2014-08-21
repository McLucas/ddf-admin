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
define(['backbone'], function (Backbone) {

    var Configuration = {};
    var configurationURL = '/jolokia/exec/org.codice.ddf.admin.application.service.ApplicationService:service=application-service/getServices/test';

    Configuration.Model = Backbone.Model.extend({
        urlRoot: function(){
            return configurationURL;
        }
    //    toJSON: function(){
    //        var modelJSON = _.clone(this.attributes);
    //        return _.extend(modelJSON,{
    //            displayName: modelJSON.name.replace('profile-','')
    //        });
    //    }
    });

    Configuration.Collection = Backbone.Collection.extend({
        model: Configuration.Model,
        initialize: function(options){
            this.type = options.type;
            this.appName = options.appName;
        },
        url: function() {
            if(this.type !== 'all'){
                return configurationURL + this.appName;
            }else{
                return configurationURL;
            }
        },
        parse: function(resp){
            return resp.value;
        }
    });

    return Configuration;
});