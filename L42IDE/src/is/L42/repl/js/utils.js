allDivs=[
  "a",
  "b",
  "c"
  ];
var doOnLoad=function (){
  hide(allDivs);
  // Get every l42 Div
  setAllAs("l42Big",{
      fontSize:"170%",
      maxLines:30000,
      mode:"ace/mode/l42",
      theme:"ace/theme/l42_eclipse",
      tabSize: 100,
      useSoftTabs: true, 
      });
  setOurMinMax();
  window.onresize=function(){setTimeout(setOurMinMax, 100);};
  }
var setOurMinMax=function(){
  var body=document.body;
  var html=document.documentElement;
  var height = Math.max(body.scrollHeight, body.offsetHeight,
    html.clientHeight, html.scrollHeight, html.offsetHeight );
  var lineHeight = height/27-1;
  setAllAs("l42Big",{
    maxLines:lineHeight,
    minLines:lineHeight
    });
  }
var toFoldAll=false;
var foldAll=function(){
  htmlFx.printOut("JS folding mode");
  toFoldAll=true;
  }
var setAllAs=function(className,options){
  var list = document.getElementsByClassName(className);
  for (var i = list.length - 1; i >= 0; i--) {
    // Turn it into an ace window and apply features
    var l42Box = ace.edit(list[i]);
    l42Box.setOptions(options);
    l42Box.setBehavioursEnabled(false);
    l42Box.setReadOnly(false);  // false to make it editable
    var text = ""+l42Box.getValue();
    l42Box.setValue(text,-1);
    l42Box.getCopyText=function(){
      var text = this.getSelectedText();
      //htmlFx.printOut("copy called on "+text);
      setTimeout(function(){htmlFx.copy(text);}, 100);
      //it was overriding the clipboard later :-(
      this._signal("copy", text);
      return text;
      };
    if(toFoldAll){
      l42Box.getSession().foldAll();
      l42Box.getSession().unfold(1,false);
      }
    }
  }

var hide=function(elements) {
  elements = elements.length ? elements : [elements];
  for (var index = 0; index < elements.length; index++) {
    var e=document.getElementById(elements[index]);
    if(e!=null){e.style.display = 'none';}
  }
}
var show=function (element) { document.getElementById(element).style.display = 'block'; }

var showAll=function(elements){
  elements = elements.length ? elements : [elements];
  for (var index = 0; index < elements.length; index++) {
    show(elements[index]);
  }
}
var selectDiv=function(id){
  hide(allDivs)
  show(id)
  }