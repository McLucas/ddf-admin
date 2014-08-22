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
    'text!/configurations/templates/serviceRow.handlebars',
    '/applications/js/view/configuration/service/config/Service.config.view.js'
],function (ich, _, Marionette, Service, ConfigurationEdit,serviceRow, ServiceConfigView) {

    if (!ich.serviceRow) {
        ich.addTemplate('serviceRow', serviceRow);
    }

     var ServiceItemView = Marionette.Layout.extend({
        template: "serviceRow",
        tagName: "tr",
        events: {
            'click .newLink' : 'newConfiguration',
            'hidden.bs.modal' : 'cancelNewConfiguration'
        },
        regions: {
            collectionRegion: '#configurationRegion',
            editModal: '.modal'
        },
        onRender: function() {
            this.collectionRegion.show(new ServiceConfigView({ collection: this.model.get("configurations") }));
        },
        newConfiguration: function() {
            if(this.model.get("factory") || this.model.get("configurations").length === 0) {
                this.model.get('response').trigger('editing');
                var configuration = new Service.Configuration();
                if(this.model.get("factory")) {
                    configuration.initializeFromMSF(this.model);
                } else {
                    configuration.initializeFromService(this.model);
                }
                this.editModal.show(new ConfigurationEdit.View({model: configuration, factory: this.model.get("factory")}));
            }
        },
        cancelNewConfiguration: function() {
            if(this.editModal.currentView) {
                this.editModal.currentView.cancel();
            }
        }
    });

    return ServiceItemView;

});