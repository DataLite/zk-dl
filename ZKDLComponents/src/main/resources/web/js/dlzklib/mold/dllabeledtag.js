(function (out) {
    var uuid = this.uuid;
    out.push('<div ', this.domAttrs_(), ' id="', uuid , '-root">');
    out.push('<span class="dl-labeled-tag-label" id="', uuid, '-label">', this.getLabel(), '</span>');
    out.push('<span class="dl-labeled-tag-value" id="', uuid, '-value">', this.getValue(), '</span>');
    out.push('<span class="dl-labeled-tag-close" id="', uuid, '-close">×</span>');
    out.push('</div>');
});