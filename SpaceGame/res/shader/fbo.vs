varying vec2 vertex;
varying vec2 texCoord;

void main() {
    vertex = gl_Vertex.xy;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    texCoord = gl_MultiTexCoord0.xy;
}