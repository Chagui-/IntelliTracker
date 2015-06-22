function hs(params,params2) {
	return yqtrackapi_j([params,params2])
	//return test([params,params2])	
}

// function test(params){
	// params.push("www.17track.net");
	// return params.join('\x7b\x45\x44\x46\x43\x45\x39\x38\x42\x2d\x31\x43\x45\x36\x2d\x34\x44\x38\x37\x2d\x38\x43\x34\x41\x2d\x38\x37\x30\x44\x31\x34\x30\x42\x36\x32\x42\x41\x7d')
// };

var l = 0;
var R = "";
function yqtrackapi_i(s) {
	return yqtrackapi_v(yqtrackapi_r(yqtrackapi_z(s)))
};
function yqtrackapi_d(s) {
	return yqtrackapi_t(yqtrackapi_r(yqtrackapi_z(s)))
};
function yqtrackapi_b(s, e) {
	return yqtrackapi_s(yqtrackapi_r(yqtrackapi_z(s)), e)
};
function yqtrackapi_h(C, d) {
	return yqtrackapi_v(yqtrackapi_q(yqtrackapi_z(C), yqtrackapi_z(d)))
};
function yqtrackapi_c(C, d) {
	return yqtrackapi_t(yqtrackapi_q(yqtrackapi_z(C), yqtrackapi_z(d)))
};
function yqtrackapi_a(C, d, e) {
	return yqtrackapi_s(yqtrackapi_q(yqtrackapi_z(C), yqtrackapi_z(d)), e)
};
function yqtrackapi_p() {
	return yqtrackapi_i("\x61\x62\x63").toLowerCase() == "\x39\x30\x30\x31\x35\x30\x39\x38\x33\x63\x64\x32\x34\x66\x62\x30\x64\x36\x39\x36\x33\x66\x37\x64\x32\x38\x65\x31\x37\x66\x37\x32"
};
function yqtrackapi_r(s) {
	return yqtrackapi_f(yqtrackapi_e(yqtrackapi_u(s), s.length * 8))
};
function yqtrackapi_q(key, data) {
	var G = yqtrackapi_u(key);
	if (G.length > 16)
		G = yqtrackapi_e(G, key.length * 8);
	var ah = Array(16),
	ab = Array(16);
	for (var i = 0; i < 16; i++) {
		ah[i] = G[i]^0x36363636;
		ab[i] = G[i]^0x5C5C5C5C
	};
	var hash = yqtrackapi_e(ah.concat(yqtrackapi_u(data)), 512 + data.length * 8);
	return yqtrackapi_f(yqtrackapi_e(ab.concat(hash), 512 + 128))
};
function yqtrackapi_v(input) {
	try {
		l
	} catch (e) {
		l = 0
	};
	var v = l ? "\x30\x31\x32\x33\x34\x35\x36\x37\x38\x39\x41\x42\x43\x44\x45\x46" : "\x30\x31\x32\x33\x34\x35\x36\x37\x38\x39\x61\x62\x63\x64\x65\x66";
	var output = "";
	var x;
	for (var i = 0; i < input.length; i++) {
		x = input.charCodeAt(i);
		output += v.charAt((x >>> 4) & 0x0F) + v.charAt(x & 0x0F)
	};
	return output
};
function yqtrackapi_t(input) {
	try {
		R
	} catch (e) {
		R = ''
	};
	var tab = "\x41\x42\x43\x44\x45\x46\x47\x48\x49\x4a\x4b\x4c\x4d\x4e\x4f\x50\x51\x52\x53\x54\x55\x56\x57\x58\x59\x5a\x61\x62\x63\x64\x65\x66\x67\x68\x69\x6a\x6b\x6c\x6d\x6e\x6f\x70\x71\x72\x73\x74\x75\x76\x77\x78\x79\x7a\x30\x31\x32\x33\x34\x35\x36\x37\x38\x39\x2b\x2f";
	var output = "";
	var H = input.length;
	for (var i = 0; i < H; i += 3) {
		var bH = (input.charCodeAt(i) << 16) | (i + 1 < H ? input.charCodeAt(i + 1) << 8 : 0) | (i + 2 < H ? input.charCodeAt(i + 2) : 0);
		for (var O = 0; O < 4; O++) {
			if (i * 8 + O * 6 > input.length * 8)
				output += R;
			else
				output += tab.charAt((bH >>> 6 * (3 - O)) & 0x3F)
		}
	};
	return output
};
function yqtrackapi_s(input, encoding) {
	var h = encoding.length;
	var i,
	O,
	q,
	x,
	V;
	var L = Array(Math.ceil(input.length / 2));
	for (i = 0; i < L.length; i++) {
		L[i] = (input.charCodeAt(i * 2) << 8) | input.charCodeAt(i * 2 + 1)
	};
	var J = Math.ceil(input.length * 8 / (Math.log(encoding.length) / Math.log(2)));
	var F = Array(J);
	for (O = 0; O < J; O++) {
		V = Array();
		x = 0;
		for (i = 0; i < L.length; i++) {
			x = (x << 16) + L[i];
			q = Math.floor(x / h);
			x -= q * h;
			if (V.length > 0 || q > 0)
				V[V.length] = q
		};
		F[O] = x;
		L = V
	};
	var output = "";
	for (i = F.length - 1; i >= 0; i--)
		output += encoding.charAt(F[i]);
	return output
};
function yqtrackapi_z(input) {
	var output = "";
	var i = -1;
	var x,
	y;
	while (++i < input.length) {
		x = input.charCodeAt(i);
		y = i + 1 < input.length ? input.charCodeAt(i + 1) : 0;
		if (0xD800 <= x && x <= 0xDBFF && 0xDC00 <= y && y <= 0xDFFF) {
			x = 0x10000 + ((x & 0x03FF) << 10) + (y & 0x03FF);
			i++
		};
		if (x <= 0x7F)
			output += String.fromCharCode(x);
		else if (x <= 0x7FF)
			output += String.fromCharCode(0xC0 | ((x >>> 6) & 0x1F), 0x80 | (x & 0x3F));
		else if (x <= 0xFFFF)
			output += String.fromCharCode(0xE0 | ((x >>> 12) & 0x0F), 0x80 | ((x >>> 6) & 0x3F), 0x80 | (x & 0x3F));
		else if (x <= 0x1FFFFF)
			output += String.fromCharCode(0xF0 | ((x >>> 18) & 0x07), 0x80 | ((x >>> 12) & 0x3F), 0x80 | ((x >>> 6) & 0x3F), 0x80 | (x & 0x3F))
	};
	return output
};
function yqtrackapi_y(input) {
	var output = "";
	for (var i = 0; i < input.length; i++)
		output += String.fromCharCode(input.charCodeAt(i) & 0xFF, (input.charCodeAt(i) >>> 8) & 0xFF);
	return output
};
function yqtrackapi_x(input) {
	var output = "";
	for (var i = 0; i < input.length; i++)
		output += String.fromCharCode((input.charCodeAt(i) >>> 8) & 0xFF, input.charCodeAt(i) & 0xFF);
	return output
};
function yqtrackapi_u(input) {
	var output = Array(input.length >> 2);
	for (var i = 0; i < output.length; i++)
		output[i] = 0;
	for (var i = 0; i < input.length * 8; i += 8)
		output[i >> 5] |= (input.charCodeAt(i / 8) & 0xFF) << (i % 32);
	return output
};
function yqtrackapi_f(input) {
	var output = "";
	for (var i = 0; i < input.length * 32; i += 8)
		output += String.fromCharCode((input[i >> 5] >>> (i % 32)) & 0xFF);
	return output
};
function yqtrackapi_e(x, H) {
	x[H >> 5] |= 0x80 << ((H) % 32);
	x[(((H + 64) >>> 9) << 4) + 14] = H;
	var a = 1732584193;
	var b = -271733879;
	var c = -1732584194;
	var d = 271733878;
	for (var i = 0; i < x.length; i += 16) {
		var bO = a;
		var bx = b;
		var bu = c;
		var az = d;
		a = yqtrackapi_l(a, b, c, d, x[i + 0], 7, -680876936);
		d = yqtrackapi_l(d, a, b, c, x[i + 1], 12, -389564586);
		c = yqtrackapi_l(c, d, a, b, x[i + 2], 17, 606105819);
		b = yqtrackapi_l(b, c, d, a, x[i + 3], 22, -1044525330);
		a = yqtrackapi_l(a, b, c, d, x[i + 4], 7, -176418897);
		d = yqtrackapi_l(d, a, b, c, x[i + 5], 12, 1200080426);
		c = yqtrackapi_l(c, d, a, b, x[i + 6], 17, -1473231341);
		b = yqtrackapi_l(b, c, d, a, x[i + 7], 22, -45705983);
		a = yqtrackapi_l(a, b, c, d, x[i + 8], 7, 1770035416);
		d = yqtrackapi_l(d, a, b, c, x[i + 9], 12, -1958414417);
		c = yqtrackapi_l(c, d, a, b, x[i + 10], 17, -42063);
		b = yqtrackapi_l(b, c, d, a, x[i + 11], 22, -1990404162);
		a = yqtrackapi_l(a, b, c, d, x[i + 12], 7, 1804603682);
		d = yqtrackapi_l(d, a, b, c, x[i + 13], 12, -40341101);
		c = yqtrackapi_l(c, d, a, b, x[i + 14], 17, -1502002290);
		b = yqtrackapi_l(b, c, d, a, x[i + 15], 22, 1236535329);
		a = yqtrackapi_m(a, b, c, d, x[i + 1], 5, -165796510);
		d = yqtrackapi_m(d, a, b, c, x[i + 6], 9, -1069501632);
		c = yqtrackapi_m(c, d, a, b, x[i + 11], 14, 643717713);
		b = yqtrackapi_m(b, c, d, a, x[i + 0], 20, -373897302);
		a = yqtrackapi_m(a, b, c, d, x[i + 5], 5, -701558691);
		d = yqtrackapi_m(d, a, b, c, x[i + 10], 9, 38016083);
		c = yqtrackapi_m(c, d, a, b, x[i + 15], 14, -660478335);
		b = yqtrackapi_m(b, c, d, a, x[i + 4], 20, -405537848);
		a = yqtrackapi_m(a, b, c, d, x[i + 9], 5, 568446438);
		d = yqtrackapi_m(d, a, b, c, x[i + 14], 9, -1019803690);
		c = yqtrackapi_m(c, d, a, b, x[i + 3], 14, -187363961);
		b = yqtrackapi_m(b, c, d, a, x[i + 8], 20, 1163531501);
		a = yqtrackapi_m(a, b, c, d, x[i + 13], 5, -1444681467);
		d = yqtrackapi_m(d, a, b, c, x[i + 2], 9, -51403784);
		c = yqtrackapi_m(c, d, a, b, x[i + 7], 14, 1735328473);
		b = yqtrackapi_m(b, c, d, a, x[i + 12], 20, -1926607734);
		a = yqtrackapi_n(a, b, c, d, x[i + 5], 4, -378558);
		d = yqtrackapi_n(d, a, b, c, x[i + 8], 11, -2022574463);
		c = yqtrackapi_n(c, d, a, b, x[i + 11], 16, 1839030562);
		b = yqtrackapi_n(b, c, d, a, x[i + 14], 23, -35309556);
		a = yqtrackapi_n(a, b, c, d, x[i + 1], 4, -1530992060);
		d = yqtrackapi_n(d, a, b, c, x[i + 4], 11, 1272893353);
		c = yqtrackapi_n(c, d, a, b, x[i + 7], 16, -155497632);
		b = yqtrackapi_n(b, c, d, a, x[i + 10], 23, -1094730640);
		a = yqtrackapi_n(a, b, c, d, x[i + 13], 4, 681279174);
		d = yqtrackapi_n(d, a, b, c, x[i + 0], 11, -358537222);
		c = yqtrackapi_n(c, d, a, b, x[i + 3], 16, -722521979);
		b = yqtrackapi_n(b, c, d, a, x[i + 6], 23, 76029189);
		a = yqtrackapi_n(a, b, c, d, x[i + 9], 4, -640364487);
		d = yqtrackapi_n(d, a, b, c, x[i + 12], 11, -421815835);
		c = yqtrackapi_n(c, d, a, b, x[i + 15], 16, 530742520);
		b = yqtrackapi_n(b, c, d, a, x[i + 2], 23, -995338651);
		a = yqtrackapi_o(a, b, c, d, x[i + 0], 6, -198630844);
		d = yqtrackapi_o(d, a, b, c, x[i + 7], 10, 1126891415);
		c = yqtrackapi_o(c, d, a, b, x[i + 14], 15, -1416354905);
		b = yqtrackapi_o(b, c, d, a, x[i + 5], 21, -57434055);
		a = yqtrackapi_o(a, b, c, d, x[i + 12], 6, 1700485571);
		d = yqtrackapi_o(d, a, b, c, x[i + 3], 10, -1894986606);
		c = yqtrackapi_o(c, d, a, b, x[i + 10], 15, -1051523);
		b = yqtrackapi_o(b, c, d, a, x[i + 1], 21, -2054922799);
		a = yqtrackapi_o(a, b, c, d, x[i + 8], 6, 1873313359);
		d = yqtrackapi_o(d, a, b, c, x[i + 15], 10, -30611744);
		c = yqtrackapi_o(c, d, a, b, x[i + 6], 15, -1560198380);
		b = yqtrackapi_o(b, c, d, a, x[i + 13], 21, 1309151649);
		a = yqtrackapi_o(a, b, c, d, x[i + 4], 6, -145523070);
		d = yqtrackapi_o(d, a, b, c, x[i + 11], 10, -1120210379);
		c = yqtrackapi_o(c, d, a, b, x[i + 2], 15, 718787259);
		b = yqtrackapi_o(b, c, d, a, x[i + 9], 21, -343485551);
		a = yqtrackapi_w(a, bO);
		b = yqtrackapi_w(b, bx);
		c = yqtrackapi_w(c, bu);
		d = yqtrackapi_w(d, az)
	};
	return Array(a, b, c, d)
};
function yqtrackapi_k(q, a, b, x, s, T) {
	return yqtrackapi_w(yqtrackapi_g(yqtrackapi_w(yqtrackapi_w(a, q), yqtrackapi_w(x, T)), s), b)
};
function yqtrackapi_l(a, b, c, d, x, s, T) {
	return yqtrackapi_k((b & c) | ((~b) & d), a, b, x, s, T)
};
function yqtrackapi_m(a, b, c, d, x, s, T) {
	return yqtrackapi_k((b & d) | (c & (~d)), a, b, x, s, T)
};
function yqtrackapi_n(a, b, c, d, x, s, T) {
	return yqtrackapi_k(b^c^d, a, b, x, s, T)
};
function yqtrackapi_o(a, b, c, d, x, s, T) {
	return yqtrackapi_k(c^(b | (~d)), a, b, x, s, T)
};
function yqtrackapi_w(x, y) {
	var ai = (x & 0xFFFF) + (y & 0xFFFF);
	var aw = (x >> 16) + (y >> 16) + (ai >> 16);
	return (aw << 16) | (ai & 0xFFFF)
};
function yqtrackapi_g(aV, bs) {
	return (aV << bs) | (aV >>> (32 - bs))
};
function yqtrackapi_j(params) {
	try {
		if (params.length == 0)
			return null;
		params.push("www.17track.net");
		return yqtrackapi_i(params.join('\x7b\x45\x44\x46\x43\x45\x39\x38\x42\x2d\x31\x43\x45\x36\x2d\x34\x44\x38\x37\x2d\x38\x43\x34\x41\x2d\x38\x37\x30\x44\x31\x34\x30\x42\x36\x32\x42\x41\x7d'))
	} catch (e) {
		return null
	}
};