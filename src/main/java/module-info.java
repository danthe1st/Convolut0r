module io.github.danthe1st.convolut0r {
	requires java.base;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	
	opens io.github.danthe1st.convolut0r to javafx.graphics;
	opens io.github.danthe1st.convolut0r.controller to javafx.fxml;
}