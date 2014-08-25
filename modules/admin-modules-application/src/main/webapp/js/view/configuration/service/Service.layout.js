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
    'icanhaz',
    'underscore',
    'marionette',
    '/applications/js/model/configuration/Configuration.js',
    '/applications/js/view/configuration/edit/Configuration.edit.layout.js',
    'text!/configurations/templates/servicePage.handlebars',
    '/applications/js/view/configuration/service/Service.collectionview.js'
],function (ich, _, Marionette, Service, ConfigurationEdit,servicePage, ServiceView) {

    if (!ich.servicePage) {
        ich.addTemplate('servicePage', servicePage);
    }

    var ServiceLayout = Marionette.Layout.extend({
        template: 'servicePage',
        events: {
            'click .refreshButton' : 'refreshServices'
        },
        regions: {
            collectionRegion: '#servicesRegion'
        },
        onRender: function() {
            this.collectionRegion.show(new ServiceView({ collection: this.model.get("value") }));
        }
    });

    return ServiceLayout;

});