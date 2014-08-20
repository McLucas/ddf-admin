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
        'jquery',
        'marionette',
        'underscore',
        'text!configurationItemViewTemplate',
        'icanhaz'
    ], function($,Marionette, _, ConfigurationItemViewTemplate, ich){
        "use strict";

        if(!ich.featureRowTemplate) {
            ich.addTemplate('configurationItemViewTemplate', ConfigurationItemViewTemplate);
        }

        var FeatureRow = Marionette.ItemView.extend({
            template: 'configurationItemViewTemplate',
            tagName: 'tr',

            initialize: function(){
                _.bindAll(this, 'onRender', 'onSelect');
            },

            onRender: function(){
                this.$el.on('click', this.onSelect);
            },

            onSelect: function(event) {
                event.stopPropagation();
                var target = $(event.target).attr("id");
                if(target !== undefined && target.indexOf('action') !== -1) {
                    this.trigger('selected', this.model);
                }
            },

            onBeforeClose: function() {
                this.$el.off('click', this.onSelect);
            }

        });

        return FeatureRow;

    }
);