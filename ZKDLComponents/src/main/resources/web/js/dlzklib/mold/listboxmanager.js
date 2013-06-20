
function (out) {
    var uuid = this.uuid,
        zcls = this.getZclass(),
        iconCount = 0;

    out.push('<div name="', uuid, '"', this.domAttrs_(), '>'); // main div
    out.push('<table class="', zcls, '-table"><tr>');

    if (this.getColumnManager()) out.push(this.getIconHtml("columnManager", "menu_items_small", iconCount++));
    if (this.getSortManager()) out.push(this.getIconHtml("sortManager", "sort_small", iconCount++));
    if (this.getFilterManager()) out.push(this.getIconHtml("filterManager", "filter_small", iconCount++));
    if (this.getResetFilters()) out.push(this.getIconHtml("resetFilters", "cancel_filter", iconCount++));
    if (this.getResetAll()) out.push(this.getIconHtml("resetAll", "trash_small", iconCount++));

    out.push('</tr></table></div>');
}
