function newImage(arg) {rslt = new Image();rslt.src = arg;return rslt;}function bOver(image) {document[image].src = eval(image +"_over.src");}function bOut(image) {document[image].src = eval(image +"_out.src");}b0_out = newImage('images/button0.png');
b0_over = newImage('images/buttonover0.png');
b1_out = newImage('images/button1.png');
b1_over = newImage('images/buttonover1.png');
b2_out = newImage('images/button2.png');
b2_over = newImage('images/buttonover2.png');
b3_out = newImage('images/button3.png');
b3_over = newImage('images/buttonover3.png');
b4_out = newImage('images/button4.png');
b4_over = newImage('images/buttonover4.png');
