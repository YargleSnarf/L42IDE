package is.L42.repl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;

public class ReplTextArea extends SplitPane {
  private static final double DIVIDER_POSN = 0.75f;
  Tab tab;
  final String tabName;
  final Path tabPath;
  final HtmlFx htmlFx;
  public ReplTextArea(CountDownLatch latch, String tabName,Path tabPath, String content) {
    assert content!=null:"";
    assert Platform.isFxApplicationThread();
    htmlFx=new HtmlFx(this);
    htmlFx.createHtmlContent(latch,wv->wv.loadContent(content));
    this.tabName=tabName;
    this.tabPath=tabPath;
    this.getItems().addAll(htmlFx);
    this.setDividerPositions(DIVIDER_POSN);
    latch.countDown();
    }
  public String getText(){
    assert Platform.isFxApplicationThread();
    return (String) htmlFx.webEngine.executeScript(
      "ace.edit(\"textArea\").getValue()"
      );
    }
  public void setText(String input){
    assert Platform.isFxApplicationThread();
    StringBuffer b=new StringBuffer();
    input.codePoints().forEachOrdered(i->{
      if(i=='\"') {b.append("\\\"");return;}
      if(i=='\\'){b.append("\\\\");return;}
      if(i=='\n'){b.append("\\n");return;}
      if(i=='\r'){return;}
      b.appendCodePoint(i);
      });
    htmlFx.webEngine.executeScript("ace.edit(\"textArea\").setValue(\""+b+"\", -1)");
    }
  boolean saveToFileAndRemoveStar() {
    try{saveToFile();}
    catch (IOException e){
      e.printStackTrace();
      addSaveError();
      return false;
      }
    removeStar();
    return true;
    }
  void saveToFile() throws IOException{
    assert Platform.isFxApplicationThread();
    String content=getText();
    Path file=this.tabPath;
    assert file!=null && Files.exists(file) : file;
    if(file.endsWith("editorStyle.js")) { parseStyle(content);} //TODO: Marco fix
    Files.write(file, content.getBytes());
    }
  void refresh() {
    assert Platform.isFxApplicationThread();
    Path file=this.tabPath;
    assert file!=null && Files.exists(file);
    String content; try {content = new String(Files.readAllBytes(file));}
    catch (IOException e) {throw new Error(e);}
    setText(content);
    removeStar();
    }
  void addStar() {
    if(!tab.getText().endsWith("*")){tab.setText(tabName+"*");}
    }
  void removeStar() {
    if(tab.getText().endsWith("*")) {tab.setText(tabName);}
    }
  void addSaveError() {
    tab.setText(tabName+"--SaveFailed");
    }
  private void parseStyle(String content) throws IOException{
  	boolean valid = Pattern.compile("[\n//]").splitAsStream(content.replace(" ", ""))
  	.filter(s->s.contains(":"))
  	.map(s->{
  		String s1 = s.substring(s.indexOf(":")+1);
  		if(s.startsWith("font:")) { return s1.matches("^[ \",'a-zA-Z]*$");}
  		else if(s.startsWith("fontSize:")) { return s1.matches("^[.,0-9]*$") 
  				&& Double.parseDouble(s1.replace(",", "")) >= 0;}
  		return s1.startsWith("\"rgb") && (s1.endsWith(")\",") || s1.endsWith(")\""));
  	})
  	.allMatch(v->v==true);
  	if(!valid) { throw new IOException("Style IO");}
    }
  //TODO Marco fix
  }
