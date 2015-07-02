(function (out) {
    var uuid = this.uuid,
        zcls = this.getZclass(),
        iconCount = 0;

    out.push('<div name="', uuid, '"', this.domAttrs_(), '>'); // main div
    out.push('<table class="', zcls, '-table"><tr>');

    if (this.getColumnManager()) out.push(this.getIconHtml("columnManager", "columns-three", iconCount++));
    if (this.getSortManager()) out.push(this.getIconHtml("sortManager", "arrow-transfer-4", iconCount++));
    if (this.getFilterManager()) out.push(this.getIconHtml("filterManager", "filter-1", iconCount++));
    if (this.getExportManager()) out.push(this.getIconHtml("exportManager", "arrow-download", iconCount++));
    if (this.getResetFilters()) out.push(this.getIconHtml("resetFilters", "recycle", iconCount++));
    if (this.getResetAll()) out.push(this.getIconHtml("resetAll", "delete-trash-1", iconCount++));

    out.push('</tr></table></div>');
})
