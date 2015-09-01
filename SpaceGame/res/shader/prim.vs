varying vec4 worldVertex;
varying vec2 texCoord;

void main() {
    worldVertex = gl_Vertex;
    gl_Position = gl_ModelViewProjectionMatrix * worldVertex;
    texCoord = gl_MultiTexCoord0.xy;
}