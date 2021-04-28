(function($) {
	var has_VML, create_canvas_for, add_shape_to, clear_canvas, shape_from_area,
		canvas_style, fader, hex_to_decimal, css3color, is_image_loaded;
	has_VML = document.namespaces;
	has_canvas = document.createElement('canvas');
	has_canvas = has_canvas && has_canvas.getContext;

    var loadedAreas = false;

    if(!(has_canvas || has_VML)) {
		$.fn.maphilight = function() { return this; };
		return;
	}

	if(has_canvas) {
		fader = function(element, opacity, interval) {
			if(opacity <= 1) {
				element.style.opacity = opacity;
				window.setTimeout(fader, 10, element, opacity + 0.1, 10);
			}
		};

		hex_to_decimal = function(hex) {
			return Math.max(0, Math.min(parseInt(hex, 16), 255));
		};
		css3color = function(color, opacity) {
			return 'rgba('+hex_to_decimal(color.substr(0,2))+','+hex_to_decimal(color.substr(2,2))+','+hex_to_decimal(color.substr(4,2))+','+opacity+')';
		};
		create_canvas_for = function(img) {
			var c = $('<canvas style="width:'+img.width+'px;height:'+img.height+'px;"></canvas>').get(0);
			c.getContext("2d").clearRect(0, 0, c.width, c.height);
			return c;
		};
		add_shape_to = function(canvas, shape, coords, options, hoverEvent) {
            var fillColor, fillOpacity, strokeWidth, strokeColor, strokeOpacity, fill, stroke, fade;
            if (typeof hoverEvent == "undefined") {
                fillColor = options.fillColor;
                fillOpacity = options.fillOpacity;
                strokeWidth = options.strokeWidth;
                strokeColor = options.strokeColor;
                strokeOpacity = options.strokeOpacity;
                fill = options.fill;
                stroke = options.stroke;
                fade = options.fade;
            }
            else{
                fillColor = options.fillColorHover;
                fillOpacity = options.fillOpacityHover;
                strokeWidth = options.strokeWidthHover;
                strokeColor = options.strokeColorHover;
                strokeOpacity = options.strokeOpacityHover;
                fill = options.fillHover;
                stroke = options.strokeHover;
                fade = options.fadeHover;
            }

            var i, context = canvas.getContext('2d');
			context.beginPath();
			if(shape == 'rect') {
				context.rect(coords[0], coords[1], coords[2] - coords[0], coords[3] - coords[1]);
			} else if(shape == 'poly') {
				context.moveTo(coords[0], coords[1]);
				for(i=2; i < coords.length; i+=2) {
					context.lineTo(coords[i], coords[i+1]);
				}
			} else if(shape == 'circ') {
				context.arc(coords[0], coords[1], coords[2], 0, Math.PI * 2, false);
			}
			context.closePath();
			if(fill) {
				context.fillStyle = css3color(fillColor, fillOpacity);
				context.fill();
			}
			if(stroke) {
				context.strokeStyle = css3color(strokeColor, strokeOpacity);
				context.lineWidth = strokeWidth;
				context.stroke();
			}
			if(fade) {
				fader(canvas, 0);
			}
		};
		clear_canvas = function(canvas, area) {
			canvas.getContext('2d').clearRect(0, 0, canvas.width,canvas.height);
		};
	} else {
		document.createStyleSheet().addRule("v\\:*", "behavior: url(#default#VML); antialias: true;");
		document.namespaces.add("v", "urn:schemas-microsoft-com:vml");

		create_canvas_for = function(img) {
			return $('<var style="zoom:1;overflow:hidden;display:block;width:'+img.width+'px;height:'+img.height+'px;"></var>').get(0);
		};
		add_shape_to = function(canvas, shape, coords, options, hoverEvent) {
            var fillColor, fillOpacity, strokeWidth, strokeColor, strokeOpacity, optionsfill, optionsstroke, fade;
            if (typeof hoverEvent == "undefined") {
                fillColor = options.fillColor;
                fillOpacity = options.fillOpacity;
                strokeWidth = options.strokeWidth;
                strokeColor = options.strokeColor;
                strokeOpacity = options.strokeOpacity;
                optionsfill = options.fill;
                optionsstroke = options.stroke;
                fade = options.fade;
            }
            else{
                fillColor = options.fillColorHover;
                fillOpacity = options.fillOpacityHover;
                strokeWidth = options.strokeWidthHover;
                strokeColor = options.strokeColorHover;
                strokeOpacity = options.strokeOpacityHover;
                optionsfill = options.fillHover;
                optionsstroke = options.strokeHover;
                fade = options.fadeHover;
            }
            var fill, stroke, opacity, e;
			fill = '<v:fill color="#'+fillColor+'" opacity="'+(optionsfill ? fillOpacity : 0)+'" />';
			stroke = (optionsstroke ? 'strokeweight="'+strokeWidth+'" stroked="t" strokecolor="#'+strokeColor+'"' : 'stroked="f"');
			opacity = '<v:stroke opacity="'+strokeOpacity+'"/>';
			if(shape == 'rect') {
				e = $('<v:rect filled="t" '+stroke+' style="zoom:1;margin:0;padding:0;display:block;position:absolute;left:'+coords[0]+'px;top:'+coords[1]+'px;width:'+(coords[2] - coords[0])+'px;height:'+(coords[3] - coords[1])+'px;"></v:rect>');
			} else if(shape == 'poly') {
				e = $('<v:shape filled="t" '+stroke+' coordorigin="0,0" coordsize="'+canvas.width+','+canvas.height+'" path="m '+coords[0]+','+coords[1]+' l '+coords.join(',')+' x e" style="zoom:1;margin:0;padding:0;display:block;position:absolute;top:0px;left:0px;width:'+canvas.width+'px;height:'+canvas.height+'px;"></v:shape>');
			} else if(shape == 'circ') {
				e = $('<v:oval filled="t" '+stroke+' style="zoom:1;margin:0;padding:0;display:block;position:absolute;left:'+(coords[0] - coords[2])+'px;top:'+(coords[1] - coords[2])+'px;width:'+(coords[2]*2)+'px;height:'+(coords[2]*2)+'px;"></v:oval>');
			}
			e.get(0).innerHTML = fill+opacity;
			$(canvas).append(e);
		};
		clear_canvas = function(canvas) {
			$(canvas).empty();
		};
	}
	shape_from_area = function(area) {
		var i, coords = area.getAttribute('coords').split(',');
		for (i=0; i < coords.length; i++) { coords[i] = parseFloat(coords[i]); }
		return [area.getAttribute('shape').toLowerCase().substr(0,4), coords];
	};

	is_image_loaded = function(img) {
		if(!img.complete) { return false; } // IE
		if(typeof img.naturalWidth != "undefined" && img.naturalWidth == 0) { return false; } // Others
		return true;
	}

	canvas_style = {
		position: 'absolute',
		left: 0,
		top: 0,
		padding: 0,
		border: 0
	};

	$.fn.maphilight = function(opts) {
		opts = $.extend({}, $.fn.maphilight.defaults, opts);
		return this.each(function() {
			var img, wrap, options, map, canvas, mouseover;
			img = $(this);
			if(!is_image_loaded(this)) { return window.setTimeout(function() { img.maphilight(); }, 200); }
			options = $.metadata ? $.extend({}, opts, img.metadata()) : opts;
			map = $('map[name="'+img.attr('usemap').substr(1)+'"]');
			if(!(img.is('img') && img.attr('usemap') && map.size() > 0 && !img.hasClass('maphilighted'))) { return; }

            wrap = $('<div>').css({display:'block',background:'url('+this.src+')',position:'relative',padding:0,width:this.width,height:this.height});

            img.before(wrap).css('opacity', 0).css(canvas_style).remove();
			if($.browser.msie) { img.css('filter', 'Alpha(opacity=0)'); }
			wrap.append(img);

			canvas = create_canvas_for(this);
			$(canvas).css(canvas_style);
			canvas.height = this.height;
			canvas.width = this.width;

			mouseover = function(e) {
				var shape = shape_from_area(this);
                add_shape_to(canvas, shape[0], shape[1], $.metadata ? $.extend({}, options, $(this).metadata()) : options);
                 $('#log').append("mouseover<br>");
            };

            var mouseoverImg = function(e) {
                $('#log').append("mouseoverImg<br>");
                if (!loadedAreas){
                    $(map).find('area[coords]').each(mouseover);
                }
                loadedAreas = true;
            };

            var mouseoutImg = function(event) {
                $('#log').append("mouseoutImg<br>");

                // check if mouse is really outside the image
                var y1 = img.offset().top;
                var y2 = img.offset().top + img.height();
                var x1 = img.offset().left;
                var x2 = img.offset().left + img.width();

                if (!(event.pageX >= x1 && event.pageX <= x2 && event.pageY >= y1 && event.pageY <= y2)) {
                    clear_canvas(canvas);
                    loadedAreas = false;
                    $('#log').append("clear canvas <hr>");
                }
            };

            var mouseoverArea = function(e) {
                $('#log').append("mouseoverArea<br>");

                var shape = shape_from_area(this);
                add_shape_to(canvas, shape[0], shape[1], $.metadata ? $.extend({}, options, $(this).metadata()) : options, true);
            };

            var mouseoutArea = function(e) {
                $('#log').append("mouseoutArea<br>");
                clear_canvas(canvas);
                if (loadedAreas){
                    $(map).find('area[coords]').each(mouseover);
                }
            };

            if (options.hoverChangeStyle){
                img.mouseover(mouseoverImg).mouseout(mouseoutImg);
                $(map).find('area[coords]').mouseover(mouseoverArea).mouseout(mouseoutArea);
            }
            else if(options.alwaysOn) {
				$(map).find('area[coords]').each(mouseover);
            } else {
				$(map).find('area[coords]').mouseover(mouseover).mouseout(function(e) { clear_canvas(canvas); });
			}

			img.before(canvas); // if we put this after, the mouseover events wouldn't fire.
			img.addClass('maphilighted');
		});
	};
    /* This lib as a bug with Chrome: we can't pass the options when calling the maphilight function */
    $.fn.maphilight.defaults = {
		fill: true,
		fillColor: 'FFFF00',
		fillOpacity: 0.2,
		stroke: true,
		strokeColor: '37576C',
		strokeOpacity: 1,
		strokeWidth: 2,
		fade: false,
		alwaysOn: false,

        hoverChangeStyle: true,
        fillHover: true,
        fillColorHover: 'FFFFFF',
        fillOpacityHover: 0.2,
        strokeHover: true,
        strokeWidthHover: 3,
        strokeColorHover: '37576C',
        strokeOpacityHover: 1,
        fadeHover: false
    };
})(jQuery);
