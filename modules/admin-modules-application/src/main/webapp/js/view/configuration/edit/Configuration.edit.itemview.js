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
        'backbone',
        'jquery',
        'marionette',
        'underscore',
        'text!configurationEditItemViewTemplate',
        'icanhaz'
    ], function(Backbone, $, Marionette, _, ConfigurationEditItemViewTemplate, ich){
        "use strict";

        if(!ich.configurationEditItemViewTemplate) {
            ich.addTemplate('configurationEditItemViewTemplate', ConfigurationEditItemViewTemplate);
        }

        var configurationItemView = Marionette.ItemView.extend({
            template: 'configurationEditItemViewTemplate',
            tagName: 'tr',
            events: {
                "click .minus-button": "minusButton"
            },
            initialize: function() {
                this.modelBinder = new Backbone.ModelBinder();
            },
            onRender: function() {
                var bindings = Backbone.ModelBinder.createDefaultBindings(this.el, 'name');
                this.modelBinder.bind(this.model, this.$el, bindings);
            },
            minusButton: function() {
                this.model.collection.remove(this.model);
            }
        });

        return configurationItemView;

    }
);