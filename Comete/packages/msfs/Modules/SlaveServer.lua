-- "Slave Server" example LUA plug-in, by Pete Dowson, October 2009
-- To make the Server FS act as a slave, following the Client FS acting as Master

-- The SOCKET module is built into FSUIPC, but is not active until "required"
socket = require("socket");
json = require("json");

-- Set the host name to the name of the PC running this Server
host = "192.168.0.16";

-- The port must match the port selected in the client and not clash with others.
port = "8384";

local function senddata(control)
	lat, lon, alt, pitch, bank, hdgT =
		ipc.readStruct(0x0560,"3DD", "2SD", "1UD")

	-- This simply sets the aircraft position and attitude
	-- according to the parameters received

	-- we need to convert from FS units
	hdgT = (((hdgT * 360) / 65536)) / 65536
	lat = (lat * 90) / (10001750 * 65536 * 65536)
	lon = (lon * 360) /  (65536 * 65536 * 65536 * 65536)
	alt = (alt * 3.28084) / (65536 * 65536)
	pitch = (pitch * 360) / (65536 * 65536)
	bank = (bank * 360) / (65536 * 65536)

	--s = "lat=" .. lat .. ",lon=" .. lon .. ",alt=" .. alt .. ",pch=" .. pitch .. ",bnk=" .. bank .. ",hdg=" .. hdgT
	s = json.encode({lat=lat,lon=lon,alt=alt,pch=pch,bnk=bank,hdg=hdgT})

	-- now send it all in one string
  control:send(s .. "\n")
end


local function processdata(command)

	-- decode string, just numbers in order with non-number separators

	--w = json.decode(command)
	--pitch = w.pitch
	--bank = w.bank
	--throttle = w.throttle

	w = string.gmatch(command, "[+-]?[0-9%.?]+")
	--lat = w()
	--lon = w()
	--alt = w()
	pitch = w()
	bank = w()
	throttle = w()
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
	pitch = (-pitch / 360) * 16383
	bank = (bank / 360) * 16383
	throttle = (throttle / 1) * 16383

	-- now write it all in one structure write
	--ipc.writeStruct(0x0560,"3DD", lat, lon, alt, "2SD", pitch, bank, "1UD", hdgT)
	--ipc.writeStruct(0x0578, "2SD", pitch, bank)
	--if pitch ~= nil then
		ipc.writeStruct(0x0BB2, "2SD", pitch)
	--end
	--if bank ~= nil then
		ipc.writeStruct(0x0BB6, "2SD", bank)
	--end
	--if throttle ~= nil then
		--print(throttle);
		ipc.writeStruct(0x089A, "2SD", throttle) -- Engine 1
		--ipc.writeStruct(0x0932, "2SD", throttle) -- Engine 2
		--ipc.writeStruct(0x09CA, "2SD", throttle) -- Engine 3
		--ipc.writeStruct(0x0A62, "2SD", throttle) -- Engine 4
	--end

end

server = assert(socket.bind(host, port));
ack = "\n";
while 1 do
    print("server: waiting for client connection...");
    control = server:accept();
    if control ~= nil then
    		print("server: client connected!");
    		while 1 do 
        		command = control:receive();
        		if command == nil or command == "quit" then
            		print("server: client disconnected");
            		ipc.control(65794) -- Pause FS
             		break
        		end
        		--print("server: command received, '" .. command .. "'");
        		assert(control:send(ack));       		
        		processdata(command)
        		senddata(control);  
        		--socket.select(nil, nil, 0.1)
    		end
    end
end
