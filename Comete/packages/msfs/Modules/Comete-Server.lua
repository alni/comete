-- "Slave Server" example LUA plug-in, by Pete Dowson, October 2009
-- To make the Server FS act as a slave, following the Client FS acting as Master

-- The SOCKET module is built into FSUIPC, but is not active until "required"
socket = require("socket");
json = require("json");
require"copas"
require"comete"

comete.init(ipc)

-- Set the host name to the name of the PC running this Server
host = "192.168.0.16";

-- The port must match the port selected in the client and not clash with others.
port = "8384";

local function senddata(control)
	lat, lon, alt, pitch, bank, hdgT =
		ipc.readStruct(0x0560,"3DD", "2SD", "1UD")

	tas = ipc.readStruct(0x02B8, "4SD")
	ias = ipc.readStruct(0x02BC, "4SD")
	vs = ipc.readStruct(0x02C8, "4SD")
	ai_pitch, ai_bank = ipc.readStruct(0x2F70, "DBL", "DBL") -- Degrees (double floating point)

	-- This simply sets the aircraft position and attitude
	-- according to the parameters received

	-- we need to convert from FS units
	hdgT = (((hdgT * 360) / 65536)) / 65536
	lat = (lat * 90) / (10001750 * 65536 * 65536)
	lon = (lon * 360) /  (65536 * 65536 * 65536 * 65536)
	alt = (alt * 3.28084) / (65536 * 65536)
	pitch = (pitch * 360) / (65536 * 65536)
	bank = (bank * 360) / (65536 * 65536)

	tas = (tas / 128)
	ias = (ias / 128)
	vs = vs*60*3.28084/256

	--s = "lat=" .. lat .. ",lon=" .. lon .. ",alt=" .. alt .. ",pch=" .. pitch .. ",bnk=" .. bank .. ",hdg=" .. hdgT
	s = json.encode({lat=lat,lon=lon,alt=alt,pch=pitch,bnk=bank,hdg=hdgT,tas=tas,ias=ias,vs=vs,ai_pch=ai_pitch,ai_bnk=ai_bank})

	--print("CometeServer: sending data: " .. s);

	-- now send it all in one string
  control:send(s .. "\n")
end

local function send_indicators(control)

	alt      = comete.get("altitude", comete.m2ft)
	tas      = comete.get("true_airspeed")
	ias      = comete.get("indicated_airspeed")
	vs       = comete.get("vertical_speed", comete.ms2ftmin)
	ai_pitch = comete.get("indicated_pitch")
	ai_bank  = comete.get("indicated_bank")
	compass  = comete.get("compass")

	s = json.encode({
		alt = alt,
		tas = tas,
		ias = ias,
		vs  = vs,
		pch = ai_pitch,
		bnk = ai_bank,
		hdg = compass	
	})

	--print("CometeServer: sending data: " .. s);

	-- now send it all in one string
	control:send(s .. "\n")
end

local function processdata(command, control)

	w = command.gmatch("%S+")
	action = w()
	offset = w()
	value = w()
	if action == "get" then
		control:send(comete.get(offset) + "\n")
	elseif action == "set" and value ~= nil then
		comete.set(offset, value)
	end


	-- decode string, just numbers in order with non-number separators

	w = json.decode(command)
	elevator = w.ele
	aileron = w.ail
	throttle = w.thr

	--w = string.gmatch(command, "[+-]?[0-9%.?]+")
	--lat = w()
	--lon = w()
	--alt = w()
	--pitch = w()
	--bank = w()
	--throttle = w()
	--hdgT = w()
	
	-- This simply sets the aircraft position and attitude
	-- according to the parameters received

	-- we need to convert to FS units
	--hdgT = (((hdgT * 65536) / 360)) * 65536
	--lat = (lat / 90) * (10001750 * 65536 * 65536)
	--lon = (lon / 360) *  (65536 * 65536 * 65536 * 65536)
	--alt = (alt / 3.28084) * (65536 * 65536)
	--pitch = (pitch / 360) * (65536 * 65536)
	--bank = (bank / 360) * (65536 * 65536)
	--pitch = (-pitch / 360) * 16383
	--bank = (bank / 360) * 16383
	--throttle = (throttle / 1) * 16383

	-- now write it all in one structure write
	--ipc.writeStruct(0x0560,"3DD", lat, lon, alt, "2SD", pitch, bank, "1UD", hdgT)
	--ipc.writeStruct(0x0578, "2SD", pitch, bank)
	if elevator ~= nil and elevator >= -1 then
		comete.set("elevator", elevator)
		--elevator = -elevator * 16383
		--ipc.writeStruct(0x0BB2, "2SD", elevator)
	end
	if aileron ~= nil and aileron >= -1 then
		comete.set("aileron", aileron)
		--aileron = aileron * 16383
		--ipc.writeStruct(0x0BB6, "2SD", aileron)
	end
	if throttle ~= nil and throttle >= -4 then
		for i=1,4 do
			comete.set("throttle"..i, throttle)
		end
		--throttle = throttle * 16383
		--ipc.writeStruct(0x089A, "2SD", throttle) -- Engine 1
		--ipc.writeStruct(0x0932, "2SD", throttle) -- Engine 2
		--ipc.writeStruct(0x09CA, "2SD", throttle) -- Engine 3
		--ipc.writeStruct(0x0A62, "2SD", throttle) -- Engine 4
	end

end

local function handler(control)
	control = copas.wrap(control)
	print("CometeServer: client connected!");
	while true do
		local command = control:receive()
		if command == nil or command == "quit" then
			print("CometeServer: client disconnected");
			ipc.control(65794) -- Pause FS
			break
		end
		print("CometeServer: command received, '" .. command .. "'");
		if command == "get" then
			senddata(control);
		elseif command == "get indicators" then
			send_indicators(control);
		else
			processdata(command);
		end
	end
end

local server = socket.bind(host, port)

copas.addserver(server, handler)

copas.loop()
