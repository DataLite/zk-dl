<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

/* global */
.z-dlzklib-clear { clear: both; }
.selectable { cursor : pointer!important; }
.nonselectable { cursor : default!important; }

/* Listbox */
.z-listbox .highlighted { background: none repeat scroll 0 0 #ec5210; color: #fff;}

/* -- Quickfilter component [1.5.x] -- */
.z-quickfilter { display: inline-block; margin-right: 20px; position: relative; width: auto; }
.z-quickfilter-button { cursor: pointer; height: 21px; margin-left: 5px!important; }
.z-quickfilter .z-quickfilter-text { margin: 4px 4px 0 0; }
.z-quickfilter .z-textbox { margin: 0 5px 0 0; }

.z-quickfilter [class^="z-icon-"], .z-quickfilter [class*=" z-icon-"] { cursor: pointer; position: relative; top: 5px }
.z-quickfilter .z-icon-menu { font-size: 11px; right: 3px; }
.z-quickfilter .z-icon-erase { color: #ff0000; position: absolute; top: 4px; margin-left: -27px; margin-right: 10px; }
.z-quickfilter .z-icon-search-1 { }

/* -- Quickfilter in lovbox -- */
/*.z-lovbox-popup .z-quickfilter { float: left; margin-bottom: 4px!important; }*/
.z-lovbox-popup .z-quickfilter-magnifier:hover { margin-top: 3px!important; }
.z-lovbox-popup .z-quickfilter [class^="z-icon-"],
.z-lovbox-popup .z-quickfilter [class*=" z-icon-"] { font-size: 16px; }
/*.z-lovbox-popup .z-quickfilter .z-icon-search-1 { margin-left: 5px; }*/
.z-lovbox-popup .z-listbox { clear: both; width: 270px; }

/* -- DLPaging component -- */
.z-dlpaging { height: auto; }
.z-dlpaging-button-table { float: left; margin-right: 30px; margin-left: 8px; width: auto; }
.z-dlpaging-aux-content { float: left; margin: 0 auto; padding-top: 2px; text-align: center; vertical-align: middle; width: auto; }

/* -- Listboxmanager component -- */
.z-listboxmanager { float: right; margin-top: 3px; }
/*.z-listboxmanager td { padding: 0 3px; }*/
.z-listboxmanager span { cursor: pointer; display: block; font-size: 16px; height: 16px; width: 19px; text-align: center; }

/* -- Listcontrol component -- */
.z-listcontrol { font: 12px Verdana, Geneva, sans-serif; padding: 0 0 3px 1px; }
.z-listcontrol-aux-content { float: left; height: 25px; width: auto; margin: 0 auto; text-align: center; vertical-align: middle; }
/*.z-listcontrol-aux-content td { padding-left: 1px; }*/
.z-listcontrol-aux-content-separator { width: 3px; }
.z-listcontrol .z-quickfilter { float: left; }

/* -- Lovbox component -- */
/*.z-lovbox { position: relative; }*/

/* -- Lovbox image -- */
.z-bandbox-label-btn { font-style: normal!important; height: 16px!important; padding: 2px 3px 1px 6px!important; }
.z-bandbox-label-btn .z-bandbox-image-btn { float: right; }

/* -- Lovbox popup -- */
.z-lovbox-pp { height: auto!important; width: auto!important; }
.z-lovbox-pp .z-bandpopup { background: #e2e2e2; padding: 3px 3px 3px 3px; height: auto!important; width: auto!important; }
.z-lovbox-pp .z-bandbox-popup-cl { border: 1px solid #7eaac6; }
.z-lovbox-pp .z-listbox { margin: 0 auto; overflow-y: auto; }
.z-lovbox-pp .z-dlpaging { padding: 0!important; }

.z-lovbox .z-bandbox-image-btn { cursor: pointer; display: block; height: 16px; width: 14px; }
.z-lovbox .z-bandbox-image-btn.z-bandbox-btn-over { border: 0 none; margin-left: 0; }
.z-lovbox .z-lovbox-del { cursor: pointer; display: none; margin-top: 8px; margin-left: -31px; padding: 5px 7px; position: absolute; }
.z-lovbox .z-lovbox-del:hover { padding: 7px 7px; margin-top: 7px; }

.z-lovbox .z-bandbox-image-btn { background: url(${c:encodeThemeURL('~./zul/img/input/bandbtn.gif')}) left -1px; }
.z-lovbox .z-lovbox-del { background: url(${c:encodeURL('~./js/dlzklib/img/clear.png')}) no-repeat; }
.z-lovbox .z-lovbox-del:hover { background: url(${c:encodeURL('~./js/dlzklib/img/clear_huge.png')}) no-repeat; }

/* -- Login -- */
.z-login { background-image:none; background-color:#8B8B8B; border: 1px solid #BDBBBB; font: 12px Verdana, Geneva, sans-serif; margin: 20px auto 0 auto; padding: 12px 15px 10px!important; }
.z-login .z-login-header { color: white; font-weight: bold; padding: 7px 5px;  }
.z-login .z-login-lbl { display: block; margin-bottom: 3px; }
.z-login input[type=text],
.z-login input[type=password] { border: 1px solid #717171; font-size: 15px; outline: none; padding: 5px 30px 5px 35px; width: 100%; }
.z-login .z-image { position: relative; top: -22px; left: 176px; }
.z-login .z-login-cm { border: 1px solid #545E6B; padding: 20px 0 30px 0;  }
.z-login .z-login-cnt { margin: 0 auto; position: relative; width: 200px; }
.z-login .z-login-rememberme { display: block; margin-top: -10px; }
.z-login .z-login-rememberme input[type=checkbox] { margin-right: 5px; position: relative; top: 1px; }
.z-login .z-login-submit { border: 1px solid #C7C7C7; color: #000; cursor: pointer; margin-top: 10px; padding: 4px 0; text-shadow: 0 1px 1px #fff; width: 100%; }
.z-login .z-login-submit:hover { border-color: #ABABAB; background-color: #EDEDED; -moz-box-shadow: 0 1px 1px rgba(0,0,0,0.1); -webkit-box-shadow: 0 1px 1px rgba(0,0,0,0.1); box-shadow: 0 1px 1px rgba(0,0,0,0.1); }
.z-login .z-login-error { color: red; margin-bottom: 15px; position: relative; width: 300px; }

/* zk8 */
.z-login .z-login-content { padding: 20px 30px 25px 30px; }
.z-login .user, .z-login .pass { margin-bottom: 7px; position: relative; }
.z-login :after { content: ""; display: block; height: 16px; width: 16px; position: absolute; top: 27px; left: 8px; }
.z-login .user:after { background: url(${c:encodeURL('~./js/dlzklib/img/user_16x16.png')}) no-repeat; }
.z-login .pass:after { background: url(${c:encodeURL('~./js/dlzklib/img/pass_16x16.png')}) no-repeat; }

/* -- Error 404 -- */
.z-404 { background:#fff; border: 1px solid #7eaac6; color: #666; font-size: 14px; margin: 20px auto 0 auto; }
.z-404 .z-404-header { border-bottom: 1px solid #BEBEBE; font-size: 12px; padding: 7px 5px;  }
.z-404 a { color: #545e6b; }
.z-404 a:hover { text-decoration: underline; }
.z-404 hr { border-color: #fff!important; margin-top: 30px;}
.z-404 p { margin: 5px 0; }
.z-404 h1.title { color: #666; font-size: 2em; font-weight: bold!important; margin-bottom: 5px; }
.z-404 .z-404-cm {  }
.z-404 .z-404-cnt { margin: 0 auto; padding: 10px 0 5px 0; position: relative; text-align: center; width: 500px; }
.z-404 .btn { border: 1px solid #ccc; border-bottom: 1px solid #b4b4b4; color: #666; margin-top: 15px; margin-bottom: 5px; padding: 6px 14px 20px 14px; text-shadow: 0 1px 1px #fff; width: auto; }
.z-404 .sad-smile { width: 100%; height: 128px; background: url(${c:encodeThemeURL('~./js/dlzklib/img/sad-smile_128x128.png')}) no-repeat; background-position:center; }

/* -- Critical error 500 (error.zul) -- */
.z-criticalError { color: #666; font-size: 14px; margin: 0 auto; overflow: hidden; z-index: 1900!important; }
.z-criticalError .z-criticalError-header { font-size: 12px; padding: 7px 5px;  }
.z-criticalError a { color: #545e6b; }
.z-criticalError a:hover { text-decoration: underline; }
.z-criticalError hr { border-color: #fff!important; margin-top: 30px;}
.z-criticalError p { font-size: 11px; margin: 5px 0; }
.z-criticalError h1.title { color: #666; font-size: 2em; font-weight: bold!important; line-height: 1; margin-bottom: 10px; }
.z-criticalError .z-criticalError-cnt { margin: 0 auto; padding: 10px 0 5px 0; position: relative; text-align: center; width: 100%; }
.z-criticalError-textarea { width: 840px; }
.z-criticalError-download { color: red; cursor: pointer; font-weight: bold; margin-right: 7px; }
.z-criticalError-download:hover { text-decoration:underline; }
.z-criticalError .globe { cursor: pointer; position: relative; top: 5px; }
.z-criticalError .btn { border: 1px solid #ccc; border-bottom: 1px solid #b4b4b4; color: #666; margin-top: 15px; margin-bottom: 5px; padding: 6px 14px 20px 14px; text-shadow: 0 1px 1px #fff; width: auto; }

/* -- CSS3 -- */
.z-quickfilter-real,
.z-login input[type=text],
.z-login input[type=password],
.z-login input[type=text]:focus,
.z-login input[type=password]:focus,
.z-login .z-login-cm,
.z-login-content,
.z-404,
.z-404 .z-404-header,
.z-404 .btn,
.z-criticalError,
.z-criticalError .z-criticalError-header,
.z-criticalError .btn { -ms-behavior: url('css/PIE/PIE.htc'); position: relative; }

/* border-radius */
.z-quickfilter-real {
-moz-border-radius: 2px 2px 2px 2px;
-webkit-border-radius: 2px 2px 2px 2px; 
border-radius: 2px 2px 2px 2px;
}
.z-404,
.z-404 .btn,
.z-criticalError,
.z-criticalError .btn {
-webkit-border-radius: 5px 5px 5px 5px;
-moz-border-radius: 5px 5px 5px 5px;
border-radius: 5px 5px 5px 5px;
}
.z-404 .z-404-header,
.z-criticalError .z-criticalError-header {
-webkit-border-radius: 5px 5px 0 0;
-moz-border-radius: 5px 5px 0 0;
border-radius: 5px 5px 0 0;
}

/* gradient */
.z-login input[type=text],
.z-login input[type=password] {
background: -moz-linear-gradient(top, rgba(201,201,202,1) 0%, rgba(255,255,255,0.79) 70%, rgba(255,255,255,1) 100%);
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,rgba(201,201,202,1)), color-stop(70%,rgba(255,255,255,0.79)), color-stop(100%,rgba(255,255,255,1)));
background: -webkit-linear-gradient(top, rgba(201,201,202,1) 0%,rgba(255,255,255,0.79) 70%,rgba(255,255,255,1) 100%);
background: -o-linear-gradient(top, rgba(201,201,202,1) 0%,rgba(255,255,255,0.79) 70%,rgba(255,255,255,1) 100%);
background: -ms-linear-gradient(top, rgba(201,201,202,1) 0%,rgba(255,255,255,0.79) 70%,rgba(255,255,255,1) 100%);
background: linear-gradient(top, rgba(201,201,202,1) 0%,rgba(255,255,255,0.79) 70%,rgba(255,255,255,1) 100%);
-pie-background: linear-gradient(top, rgba(201,201,202,1) 0%,rgba(255,255,255,0.79) 70%,rgba(255,255,255,1) 100%);
}
.z-login .z-login-cm, .z-login-content/*zk8*/ {
background: #e2e2e2;
background: -moz-linear-gradient(top, #e2e2e2 0%, #fafafa 100%);
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#e2e2e2), color-stop(100%,#fafafa));
background: -webkit-linear-gradient(top, #e2e2e2 0%,#fafafa 100%);
background: -o-linear-gradient(top, #e2e2e2 0%,#fafafa 100%);
background: -ms-linear-gradient(top, #e2e2e2 0%,#fafafa 100%);
background: linear-gradient(top, #e2e2e2 0%,#fafafa 100%);
-pie-background: linear-gradient(top, #e2e2e2 0%,#fafafa 100%);
}
/*.z-login .z-login-submit {*/
/*background: #ffffff;*/
/*background: -moz-linear-gradient(top, #ffffff 0%, #d7d7d7 100%);*/
/*background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#ffffff), color-stop(100%,#d7d7d7));*/
/*background: -webkit-linear-gradient(top, #ffffff 0%,#d7d7d7 100%);*/
/*background: -o-linear-gradient(top, #ffffff 0%,#d7d7d7 100%);*/
/*background: -ms-linear-gradient(top, #ffffff 0%,#d7d7d7 100%);*/
/*background: linear-gradient(top, #ffffff 0%,#d7d7d7 100%);*/
/*-pie-background: linear-gradient(top, #ffffff 0%,#d7d7d7 100%);*/
/*}*/
/*.z-login .z-login-submit:hover {*/
/*background: #f6fcfe;*/
/*background: -moz-linear-gradient(top, #f6fcfe 0%, #badbec 100%);*/
/*background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#f6fcfe), color-stop(100%,#badbec));*/
/*background: -webkit-linear-gradient(top, #f6fcfe 0%,#badbec 100%);*/
/*background: -o-linear-gradient(top, #f6fcfe 0%,#badbec 100%);*/
/*background: -ms-linear-gradient(top, #f6fcfe 0%,#badbec 100%);*/
/*background: linear-gradient(top, #f6fcfe 0%,#badbec 100%);*/
/*-pie-background: linear-gradient(top, #f6fcfe 0%,#badbec 100%);*/
/*}*/
.z-404 .z-404-header {
background: #f6f8f9;
background: -moz-linear-gradient(top, #f6f8f9 0%, #e5ebee 50%, #d7dee3 51%, #f5f7f9 100%)!important;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#f6f8f9), color-stop(50%,#e5ebee), color-stop(51%,#d7dee3), color-stop(100%,#f5f7f9))!important;
background: -webkit-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: -o-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: -ms-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
-pie-background: linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
}
.z-404 .btn {
background: #f3f3f3;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #f3f3f3), color-stop(50%, #dddddd), color-stop(50%, #d2d2d2), color-stop(100%, #dfdfdf));
background: -webkit-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -moz-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -ms-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -o-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
-pie-background: linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
}
.z-404 .btn:hover {
background: #e5e5e5;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #e5e5e5), color-stop(50%, #d1d1d1), color-stop(50%, #c4c4c4), color-stop(100%, #b8b8b8));
background: -webkit-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -moz-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -ms-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -o-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
-pie-background: linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
}
.z-criticalError .z-criticalError-header {
background: #f6f8f9;
background: -moz-linear-gradient(top, #f6f8f9 0%, #e5ebee 50%, #d7dee3 51%, #f5f7f9 100%)!important;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#f6f8f9), color-stop(50%,#e5ebee), color-stop(51%,#d7dee3), color-stop(100%,#f5f7f9))!important;
background: -webkit-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: -o-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: -ms-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
-pie-background: linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
}
.z-criticalError .btn {
background: #f3f3f3;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #f3f3f3), color-stop(50%, #dddddd), color-stop(50%, #d2d2d2), color-stop(100%, #dfdfdf));
background: -webkit-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -moz-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -ms-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -o-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
-pie-background: linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
}
.z-criticalError .btn:hover {
background: #e5e5e5;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #e5e5e5), color-stop(50%, #d1d1d1), color-stop(50%, #c4c4c4), color-stop(100%, #b8b8b8));
background: -webkit-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -moz-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -ms-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -o-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
-pie-background: linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
}

/* box-shadow */
.z-login .z-login-submit:active {
-moz-box-shadow: inset 0 0 10px 0 #fff, 0 1px 0 0 white;
-webkit-box-shadow: inset 0 0 10px 0 #fff, 0 1px 0 0 white;
box-shadow: inset 0 0 10px 0 #fff, 0 1px 0 0 white;
}
.z-login input[type=text]:focus,
.z-login input[type=password]:focus {
-moz-box-shadow: 0px 0px 5px #007eff;
-webkit-box-shadow: 0px 0px 5px #007eff;
box-shadow: 0px 0px 5px #007eff;
}
.z-login input.error {
-moz-box-shadow: 0px 0px 5px red;
-webkit-box-shadow: 0px 0px 5px red;
box-shadow: 0px 0px 5px red;
}
.z-login input.error:focus {
-moz-box-shadow: 0px 0px 5px #007eff;
-webkit-box-shadow: 0px 0px 5px #007eff;
box-shadow: 0px 0px 5px #007eff;
}
.z-404 .btn:active,
.z-criticalError .btn:active {
-moz-box-shadow: inset 0 0 30px 0 #999999, 0 1px 0 0 white;
-webkit-box-shadow: inset 0 0 30px 0 #999999, 0 1px 0 0 white;
box-shadow: inset 0 0 30px 0 #999999, 0 1px 0 0 white;
}