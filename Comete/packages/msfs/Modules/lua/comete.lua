-- Required before call to "module()"
-- See: http://stackoverflow.com/a/12785960/1971389
local ipc = ipc
local print = print
local tonumber = tonumber
local string = string
local math = math

module("comete")

--local ipc = nil
--local print = nil

--function init(_ipc, _print)
	--ipc = _ipc
	--print = _print
--end



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
	throttle4          = {0x0A62, "2SD", 16383},
	true_airspeed      = {0x02B8, "4SD", 128},
	indicated_airspeed = {0x02BC, "4SD", 128},
	vertical_speed     = {0x02C8, "4SD", 256},
	compass            = {0x02CC, "DBL", 1}, -- compass heading DEG
	indicated_pitch    = {0x2F70, "DBL", 1}, -- attitude indicator pitch DEG
	indicated_bank     = {0x2F78, "DBL", 1}, -- attitude indicator bank DEG

	-- frequency, 4 digits in BCD format. A frequency of 123.45 is represented
	-- by 0x2345. The leading 1 is assumed
	COM1               = {0x034E, "2UD", "BCD", true},
	NAV1               = {0x0350, "2UD", "BCD", true},
	NAV2               = {0x0352, "2UD", "BCD", true},
	-- Transponder setting, 4 digits in BCD format: 0x1200 means 1200 on the dials.
	ADF1               = {0x0352, "2UD", "BCD", false}, --transponder
};

function init()
	print("INIT Start!")
	offsets["/controls/engines/engine[0]/throttle"] = {0x089A, "2SD", 16383}
	offsets["/controls/engines/engine[1]/throttle"] = {0x0932, "2SD", 16383}
	offsets["/controls/engines/engine[2]/throttle"] = {0x09CA, "2SD", 16383}
	offsets["/controls/engines/engine[3]/throttle"] = {0x0A62, "2SD", 16383}

	offsets["/controls/flight/flaps"] = {0x0BDC, "4UD", 16383}
	offsets["/controls/flight/rudder"] = {0x0BBA, "2SD", 16383}
	offsets["/controls/flight/aileron"] = {0x0BB6, "2SD", 16383}
	offsets["/controls/flight/elevator"] = {0x0BB2, "2SD", -16383}

	offsets["/controls/gear/gear-down"] = {0x0BE8, "4UD", 16383}
	offsets["/controls/gear/brake-parking"] = {0x0BC8, "2UD", 32767}
	offsets["/controls/gear/brake-left"] = {0x0C00, "1UD", 200}
	offsets["/controls/gear/brake-right"] = {0x0C01, "1UD", 200}

	offsets["/velocities/airspeed-kt"] = {0x02BC, "4SD", 128}
	offsets["/orientation/heading-deg"] = {0x02CC, "DBL", 1}

	-- NAV1
	offsets["/instrumentation/nav/frequencies/selected-mhz"] = {0x0352, "2UD", "BCD", true}
	offsets["/instrumentation/nav/radials/selected-deg"] = {}
	offsets["/instrumentation/nav[0]/frequencies/selected-mhz"] = {0x0352, "2UD", "BCD", true}
	offsets["/instrumentation/nav[0]/radials/selected-deg"] = {}

	-- NAV2
	offsets["/instrumentation/nav[1]/frequencies/selected-mhz"] = {0x0352, "2UD", "BCD", true}
	offsets["/instrumentation/nav[1]/radials/selected-deg"] = {}

	-- ADF1
	offsets["/instrumentation/adf/frequencies/selected-khz"] = {0x0352, "2UD", "BCD", false}

	-- COM1
	offsets["/instrumentation/comm/frequencies/selected-mhz"] = {0x034E, "2UD", "BCD", true}
	offsets["/instrumentation/comm[0]/frequencies/selected-mhz"] = {0x034E, "2UD", "BCD", true}

	offsets["/instrumentation/attitude-indicator/indicated-pitch-deg"] = {0x2F70, "DBL", 1}
	offsets["/instrumentation/attitude-indicato/indicated-roll-deg"] = {0x2F78, "DBL", 1} --bank

	offsets["/instrumentation/airspeed-indicator/ndicated-speed-kt"] = {0x02BC, "4SD", 128}

	offsets["/instrumentation/heading-indicator/indicated-heading-deg"] = {0x02CC, "DBL", 1}

	offsets["/instrumentation/altimeter/indicated-altitude-ft"] = {0x0574, "2SD", (65536 * 65536), m2ft(1)}

	offsets["/instrumentation/vertical-speed-indicator/indicated-speed-fpm"] = {0x02C8, "4SD", 256, ms2ftmin(1)}

	offsets["/sim/current-view/view-number"] = {}

	print("INIT END!")
end

--- Returns HEX representation of num
-- Referred by "Number to HEX": http://snipplr.com/view/13086
function num2hex(num)
    local hexstr = '0123456789abcdef'
    local s = ''
    while num > 0 do
        local mod = math.fmod(num, 16)
        s = string.sub(hexstr, mod+1, mod+1) .. s
        num = math.floor(num / 16)
    end
    if s == '' then s = '0' end
    return s
end


function toBCD(str, is_freq)
	local bcd = ""
	local l = #str
	local i = 0
	if is_freq then
		if l == 6 then
			i = 2
		elseif l == 5 then
			i = 1
		end

		if i == 0 then
			return false
		end

		bcd = bcd .. str:sub(i,i+1) .. str:sub(i+3)
	else
		bcd = bcd .. str
	end
	print("CometeServer: toBCD() = '"..bcd .. "'")
	return tonumber(bcd, 16) 
end

function fromBCD(val, is_freq)
	local str = num2hex(val)
	if is_freq then
		str = "1" .. str:sub(1,2) .. "." .. str:sub(3,2)
	end
	return str
end


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
	if offset and offset[1] and offset[2] and offset[3] then
		val = ipc.readStruct(offset[1], offset[2])
		if offset[3] == "BCD" then
			return fromBCD(val, offset[4])
		else
			if offset[4] then
				conv = offset[4]
			end
			return (val*conv)/offset[3]
		end
	end
end

function set(str, val)
	offset = offsets[str]
	if offset and offset[1] and offset[2] and offset[3] then
		if offset[3] == "BCD" then
			print("CometeServer: SETTING '"..str .. "' to '"..val.."'")
			ipc.writeStruct(offset[1], offset[2], toBCD(val, offset[4]))
		else
			ipc.writeStruct(offset[1], offset[2], val*offset[3])
		end
	end
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

function between(val, min, max)
	return val ~= nil and val >= min and val <= max
end