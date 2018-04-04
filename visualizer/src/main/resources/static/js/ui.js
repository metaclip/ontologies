function init(){
	Dropzone.options.myDropzone = {
		url: '/image', 
		maxFiles:1,
		acceptedFiles:'image/png',
		dictDefaultMessage:'Drop your image outcome here',
		success:function(file,response){
			document.getElementById('dropzone').style('display','none');
			document.getElementById('loader').style('display','block');
			document.getElementById('nav').style('display','block');
			initGraph(response);
		},
		error:function(file,response){
			// TODO prettify
			alert(response);
		}
	};
}
function initGraph(data){
	document.getElementById('loader').style.display='none';
	var graph = new MetaclipD3('#metaclip-graph',{
		minCollision: 60,
		graphData: data,
		nodeRadius: 25,
        onNodeDragEnd:function(){
        	if(!document.getElementById('animationCheckbox').checked){
        		graph.stopSim();
        	}
        },
        onRelationshipDoubleClick: function(relationship) {
            console.log('double click on relationship: ' + JSON.stringify(relationship));
        },
        filters:filters(),
		zoomFit: true
	});
	document.getElementById('animationCheckbox').onchange = function(e){
		if(e.target.checked){
			graph.startSim();
		}else{
			graph.stopSim();
		}
	}
	document.getElementById('fitButton').onclick = function(e){
		graph.zoomFit();
	}
	var filterEls = document.getElementsByName('filter');
	filterEls.forEach(function(e){
		e.onchange = function(d){
			var ffs = filters();
			if(d.target.checked){
				if(e.attributes['data-required']){
					ffs[e.attributes['data-required'].value] = true;
				}							
			}
			ffs[d.target.value] = d.target.checked;
			graph.update(ffs);
		}
	});
	function filters(){
		var filters = {};
		var filterEls = document.getElementsByName('filter');
		filterEls.forEach(function(e){
			filters[e.value] = e.checked;
		});
		return filters;
	}
}
window.onload = init;