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
/*global define, window*/
define([
        'jquery',
        'marionette',
        'underscore',
        'text!configurationItemViewTemplate',
        'icanhaz',
        '/applications/js/view/configuration/edit/Configuration.edit.layout.js'
    ], function($,Marionette, _, ConfigurationItemViewTemplate, ich, ConfigurationEditViewLayout){
        "use strict";

        if(!ich.configurationItemViewTemplate) {
            ich.addTemplate('configurationItemViewTemplate', ConfigurationItemViewTemplate);
        }

        var FeatureRow = Marionette.Layout.extend({
            template: "configurationItemViewTemplate",
            tagName: "tr",
            events: {
                'click .editLink' : 'editConfiguration',
                'click .removeLink' : 'removeConfiguration',
                'hidden.bs.modal' : 'cancelEditConfiguration',
                'shown.bs.modal' : 'refreshConfiguration'
            },
            regions: {
                editModal: '.modal'
            },
            onRender: function() {
                var layout = new ConfigurationEditViewLayout({model: this.model, factory: !_.isUndefined(this.model.get("fpid"))});
                this.editModal.show(layout);
            },
            editConfiguration: function() {
                this.model.get('service').get('response').trigger('editing');
            },
            removeConfiguration: function() {
                var question = "Are you sure you want to remove the configuration: "+this.model.get("service.pid")+"?";
                var confirmation = window.confirm(question);
                if(confirmation) {
                    this.model.destroy();
                    this.close();
                }
            },
            cancelEditConfiguration: function() {
                if(this.editModal.currentView) {
                    this.editModal.currentView.cancel();
                }
            },
            refreshConfiguration: function() {
                if(this.editModal.currentView) {
                    this.editModal.currentView.refresh();
                }
            }
        });

        return FeatureRow;

    }
);