dlzklib.Listboxmanager = zk.$extends(zul.inp.InputWidget, {
    _columnManager: true,
    _sortManager: true,
    _filterManager: true,
    _exportManager: true,
    _resetFilters: true,
    _resetAll: true,
    _filterTooltip: '',

    $define: {
        columnManager: _zkf = function (val) {
            if (this.desktop)
                this.rerender();
        },
        sortManager: _zkf,
        filterManager: _zkf,
        exportManager: _zkf,
        resetFilters: _zkf,
        resetAll: _zkf,

        filterTooltip: function (val) {
            if (this.desktop)
                this.rerender();
        }
    },

    bind_: function () {
        this.$supers(dlzklib.Listboxmanager,'bind_', arguments);

        var childs = jq(this.$n()).find('span'),
            i = childs.length;

        while (i-- > 0) {
            this.domListen_(childs[i], 'onClick', '_doClick');
        }
    },
    unbind_: function () {
        var childs = jq(this.$n()).find('span'),
            i = childs.length;

        while (i-- > 0) {
            this.domUnlisten_(childs[i], 'onClick', '_doClick');
        }

        this.$supers(dlzklib.Listboxmanager,'unbind_', arguments);
    },

    _doClick: function (evt) {
        var name= evt.domTarget.id.replace(/.*\-/,"");

        var eventName = "on" + name.substr(0,1).toUpperCase() + name.substr(1);

        this.fire(eventName, "", {});
    },

    getIconHtml: function( menu, icon, iconCount ) {
        var html = [],
            tooltip = msgdlzklib[menu + "_tooltip"];

        if (menu === "filterManager" && this.getFilterTooltip()) {
            icon = "filter_small_active";
            tooltip = this.getFilterTooltip();
        }

        if (iconCount)
            html.push('<td>|</td>');

        html.push('<td><span class="z-icon-', icon, '" id="', this.uuid, '-', menu, '"',
            'title="', tooltip, '"></span></td>');
        return html.join('');
    }
});
