uniform sampler2D texture;

varying vec4 vertex;
varying vec2 texCoord;

void main() {
	gl_FragColor = vec4(1.0) - texture2D(texture, texCoord);
}