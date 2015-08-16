(function (out) {
    var uuid = this.uuid,
        zcls = this.getZclass(),
        pageCount = (this.getKnownPageCount() ? this.getPageCount() : '?'); // unknown page count

    out.push('<div class="z-dlpaging"><div name="', uuid, '"', this.domAttrs_(), '>'); // main div

    // table with buttons
    out.push('<div class="z-dlpaging-button-table">');

    out.push('<table', zUtl.cellps0, '><tbody><tr>');

    // button-first
    out.push('<td>');
    out.push('<table id="', uuid, '-first" name="', uuid, '-first"',
        zUtl.cellps0, ' class="', zcls, '-btn"><tbody><tr>',
        '<td class="', zcls, '-btn-l"></td>',
        '<td class="', zcls, '-btn-m"><em unselectable="on">');
    out.push('<button type="button" class="', zcls, '-first"> </button>');
    out.push('</em></td>', '<td class="', zcls, '-btn-r"></td></tr></tbody></table>');
    out.push('</td>');

    // button-prev
    out.push('<td>');
    out.push('<table id="', uuid, '-prev" name="', uuid, '-prev"', zUtl.cellps0,
        ' class="', zcls, '-btn"><tbody><tr><td class="', zcls, '-btn-l"></td>',
        '<td class="', zcls, '-btn-m"><em unselectable="on">');
    out.push('<button type="button" class="',
        zcls, '-prev"> </button>');
    out.push('</em></td><td class="', zcls, '-btn-r"></td>', '</tr></tbody></table>');
    out.push('</td><td><span class="', zcls, '-sep"></span></td>',
        '<td><span class="', zcls, '-text"></span></td>');

    // input active page
    out.push('<td>');
    out.push('<input id="', uuid, '-real" name="', uuid, '-real" type="text" class="',
        zcls, '-inp" value="', this.getActivePage() + 1, '" size="3"/>');
    out.push('</td>',
        '<td><span class="', zcls, '-text">/ ', pageCount, '</span></td>',
        '<td><span class="', zcls, '-sep"></span></td>');

    // button-next
    out.push('<td>');
    out.push('<table id="', uuid,
        '-next" name="', uuid, '-next"', zUtl.cellps0, ' class="', zcls, '-btn">',
        '<tbody><tr><td class="', zcls, '-btn-l"></td><td class="',
        zcls, '-btn-m"><em unselectable="on">');
    out.push('<button type="button" class="',
        zcls, '-next"> </button></em></td><td class="', zcls, '-btn-r"></td>',
        '</tr></tbody></table>');
    out.push('</td>');

    // button-last
    out.push('<td>');
    out.push('<table id="', uuid, '-last" name="',
        uuid, '-last"', zUtl.cellps0, ' class="', zcls, '-btn"><tbody><tr>',
        '<td class="', zcls, '-btn-l"></td><td class="', zcls,
        '-btn-m"><em unselectable="on">');
    out.push('<button type="button" class="', zcls,
        '-last"> </button></em></td><td class="', zcls, '-btn-r"></td>');
    out.push('</tr></tbody></table>');

    out.push('</td>');

    out.push('</tr></tbody></table>');

    out.push('</div>'); // /div z-dlpaging-button-table


    // any children
    out.push('<div class="z-dlpaging-aux-content">');
    this.redrawChildren(out);
    out.push('</div>');

    if (this.isDetailed()) out.push(this._infoTags());
    out.push('<br class="z-dlzklib-clear"/></div></div>');
})
