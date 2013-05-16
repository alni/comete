module("comete")

local ipc = nil

function init(_ipc)
	ipc = _ipc
end



-- Last Value in array used to convert from/to FS units
-- TO: "multiply by X" - "FS = REAL * X"
-- FROM: "divide by X" - "REAL = FS / X"
local offsets = {
	altitude           = {0x0574, "2SD", (65536 * 65536)},


	elevator           = {0x0BB2, "2SD", -16383},
	aileron            = {0x0BB6, "2SD", 16383},
	throttle1          = {0x089A, "2SD", 16383},
	throttle2          = {0x0932, "2SD", 16383},
	throttle3          = {0x09CA, "2SD", 16383},
	throttle4          = {0x09CA, "2SD", 16383},
	true_airspeed      = {0x02B8, "4SD", 128},
	indicated_airspeed = {0x02BC, "4SD", 128},
	vertical_speed     = {0x02C8, "4SD", 256},
	compass            = {0x02CC, "DBL", 1}, -- compass heading DEG
	indicated_pitch    = {0x2F70, "DBL", 1}, -- attitude indicator pitch DEG
	indicated_bank     = {0x2F78, "DBL", 1} -- attitude indicator bank DEG
};


function ms2ftmin(val) 
	return val*60*3.28084
end

function m2ft(val)
	return val*3.28084
end

function get(str, conv_func)
	conv = 1
	if conv_func ~= nil then
		conv = conv_func(1)
	end
	offset = offsets[str]
	val = ipc.readStruct(offset[1], offset[2])
	return (val*conv)/offset[3]
end

function set(str, val)
	offset = offsets[str]
	ipc.writeStruct(offset[1], offset[2], val*offset[3])
end

function get_indicators()
	ias = get("indicated_airspeed")
	ai_pitch = get("indicated_pitch")
	ai_bank = get("indicated_bank")
	alt = m2ft(get("altitude"))
	hdg = get("compass")
	vs = ms2ftmin(get("vertical_speed"))
	return {
		ias = ias,
		pch = ai_pitch,
		bnk = ai_bank,
		alt = alt,
		hdg = hdg,
		vs = vs
	}
end