// CrossMark Dialog Box v1.4

// Create crossmark object
var crossmark = {

	sOpenSelector: '#open-crossmark',
	sDOI: '',
	sDomain: '',
	sURIScheme: '',
	sCrossMarkURL: '',
	sStylesURL: '',
	sTooltipID: 'crossmark-tooltip-130',
	sTooltipSelector: '#crossmark-tooltip-130',
	sTooltipCopy: 'Click to get updates and verify authenticity.',

	initialize: function() {
		this.sDOI = this.detectDOI();
		this.sDomain = window.location.hostname;
		this.sURIScheme = window.location.protocol;
		this.sCrossMarkURL = '{{consts.crossmark-server}}' + '/dialog/?doi=' + this.sDOI + '&domain=' + this.sDomain + '&uri_scheme=' + this.sURIScheme + '&cm_version=' + this.scriptVersion();
		this.sStylesURL = '{{consts.cdn-url}}' + '/stylesheets/' + this.versionDir() + 'crossmark_widget.css';
	},
  
	addStylesheet: function() {
		$cmjq(this.sOpenSelector).parent().eq(0).prepend("<link media='screen' rel='stylesheet' type='text/css' href='" + this.sStylesURL + "'/>");
	},

	activateTooltip: function() {
		var that = this;
		$cmjq('body').append('<div id="' + this.sTooltipID + '" class="crossmark-tooltip" style="display: none;"><div class="cmtttop"></div><div class="cmttmid"><p>' + this.sTooltipCopy + '</p></div><div class="cmttbot"></div></div>');
		var $icon = $cmjq('#crossmark-icon');
		$icon.attr({'title': '', 'alt': ''}).show();
		$icon.mouseover(function(){
			var o = $icon.offset();
			var x = o.left + $icon.width()/2 - $cmjq('#crossmark-icon').width()/2;
			var y = o.top - $cmjq(that.sTooltipSelector).height() + 10;
			$cmjq(that.sTooltipSelector).css({ 'left' : x, 'top' : y }).show();
		});
		$icon.mouseout(function(){
			$cmjq(that.sTooltipSelector).hide();
		});
	},

	scriptVersion: function() {
		var script = $cmjq('script[src$="crossmark.js"]')[0] || $cmjq('script[src$="crossmark.min.js"]')[0];
		var scriptName = script.src
		var parts = scriptName.split("/");
		var version = parts[parts.length - 2] == 'javascripts' ? 'v1.4' : parts[parts.length -2]
		return version;

	},

	versionDir: function() {
		var version = this.scriptVersion();
		var dir = version == 'v1.4' ? '' : version + '/';
		return dir;

	},
  

	activateDialog: function() {
		var that = this;
		$cmjq(this.sOpenSelector).click(function(){
			$cmjq('#crossmark-dialog-frame').attr('src', that.sCrossMarkURL);
			$cmjq('#crossmark-dialog').dialog("open");
			$cmjq(that.sTooltipSelector).hide();
			return false;
		});
	},

	detectDOI: function() {
		this.sDOI = '';
		var dc_id = $cmjq('meta').filter(function() {
			var $this = $cmjq(this);
			var isIdentifier = (/dc\.identifier/i).test($this.attr('name'));
			var isDOI = (/^info:doi/i).test($this.attr('content')) ||
						(/^doi/i).test($this.attr('content')) ||
						(/^10\./i).test($this.attr('content')) ||
						(/dx.doi.org\./i).test($this.attr('content'));

			return isIdentifier && isDOI;
		}).attr("content");
		
		if (dc_id) {
			dc_id = dc_id.replace(/^info:doi\//, ''); // Nature style
			dc_id = dc_id.replace(/^doi:/, ''); // IngentaConnect style
		}

		return dc_id;
	}
};

var $cmjq = jQuery.noConflict(); // assign the main jQuery object to $cmjq

jQuery(function($) {

	// Define CrossMark Dialog
	$cmjq('#crossmark-dialog').dialog({
		zIndex: 3999,
		autoOpen: false,
		// Set to true for PDF case
		modal: true,
		resizable: false,
		draggable: false,
		open: function() {
			$cmjq(".ui-widget-overlay").click(function(){
				$cmjq('#crossmark-dialog').dialog("close");
			});
		},
		beforeClose: function() {
			$cmjq(".ui-widget-overlay").unbind();
		},
		height: 550,
		width: 550,
		dialogClass: "crossmark-ui-dialog"
	});

	// Initialize Crossmark Object
	crossmark.initialize();
	crossmark.addStylesheet();
	crossmark.activateTooltip();
	crossmark.activateDialog();

});
