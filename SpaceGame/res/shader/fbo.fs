const vec2 vec = vec2(0.002);
const float blurMin = 0.1;
const mat3 edge = mat3(0.0, 1.0, 0.0,
						1.0, -4.0, 1.0,
						0.0, 1.0, 0.0),
			blur = mat3(1.0, 2.0, 1.0,
						2.0, 4.0, 2.0,
						1.0, 2.0, 1.0) / 16.0;

uniform sampler2D texture;

varying vec4 vertex;
varying vec2 texCoord;

vec3 convul(vec2 texCoord, mat3 kernel) {
	vec3 color = vec3(0.0);
    for(int x = 0; x < 3; x++) {
    	for(int y = 0; y < 3; y++) {
    		vec2 modTexCoord = texCoord + vec * (vec2(x, y) - 1.0);
    		color += kernel[x][y] * texture2D(texture, modTexCoord).rgb;
    	}
    }
    return color;
}

void main() {
	vec4 color = texture2D(texture, texCoord);
	if(color.a == 0.0) return;
    vec3 colorDif = convul(texCoord, edge);
    float dif = dot(colorDif, vec3(1.0)) / 3.0;
    if(dif > blurMin) {
    	gl_FragColor = vec4(convul(texCoord, blur), 1.0);
    } else {
    	gl_FragColor = color;
    }
}