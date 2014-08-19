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
/* global define */
define([
    'marionette',
    'backbone',
    'icanhaz',
    '/applications/js/view/IFrameView.js',
    'text!pluginTabContentItemView',
    'text!pluginTabContentCollectionView'
    ],function (Marionette, Backbone, ich, IFrameView, pluginTabContentItemView, pluginTabContentCollectionView) {

    ich.addTemplate('pluginTabContentItemView', pluginTabContentItemView);
    ich.addTemplate('pluginTabContentCollectionView', pluginTabContentCollectionView);

    var ItemView = Marionette.Layout.extend({
        template: 'pluginTabContentItemView',
        className: 'tab-pane fade',
        regions: {
            tabContentInner: '.tab-content-inner'
        },
        onBeforeRender: function(){
            this.$el.attr('id', this.model.get('displayName'));
        },
        onRender: function(){
            var view = this;
            view.model.set('jsLocation','/applications/js/view/plugins/config/Plugin.view.js');
            var iframeLocation = view.model.get('iframeLocation');
            var jsLocation = view.model.get('jsLocation');
            if(jsLocation){
                require([jsLocation], function(TabView){
                    var newView = new TabView({model: view.model});
                    view.tabContentInner.show(newView);
                });
            } else if(iframeLocation){
                view.tabContentInner.show(new IFrameView({
                    model: new Backbone.Model({url : iframeLocation})
                }));
            }
        }
    });

    var PluginTabContentsView = Marionette.CollectionView.extend({
        className: 'tab-content',
        itemView: ItemView
    });

    return PluginTabContentsView;
});