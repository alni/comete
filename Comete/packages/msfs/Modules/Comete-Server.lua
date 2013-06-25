-- Comete Server Library - Control MSFS with an Android device
-- Copyright (C) 2011-2013  Alexander Nilsen
-- 
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.


-- Comete Server Library by alni
-- =============================
-- Version 0.5 
-- for FSUIPC
--
---------------------------------------------------------------------
-- Usage: * save to MSFS/Modules/
--        * change the HOST to the IP Address of your computer
--        * optionaly change the PORT
---------------------------------------------------------------------
-- # CONFIGURATION

HOST = "192.168.0.16"
PORT = "8384"

---------------------------------------------------------------------
---------------------------------------------------------------------
-- # MAIN PROGRAM
-- DO NOT TOUCH THE CODE BELOW UNLESS YOU KNOW WHAT YOU ARE DOING
---------------------------------------------------------------------

socket = require("socket");
json = require("json");
require"copas"

package.loaded[ 'comete' ] = nil
require 'comete'

comete.init(ipc, print)

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
	
	
	if command:sub(1,3) == "get" or command:sub(1,3) == "set" then
		w = {}
		for word in command:gmatch("%S+") do table.insert(w, word) end
		action = w[1]
		offset = w[2]
		value = w[3]
		if action == "get" then
			control:send(comete.get(offset) .. "\n")
		elseif action == "set" and value ~= nil then
			comete.set(offset, value)
		end
	elseif command ~= "data" then
		-- decode string as json
		w = json.decode(command)
		elevator = w.ele
		aileron = w.ail
		throttle = w.thr

		if comete.between(elevator, -1, 1) then
			comete.set("elevator", elevator)
		end
		if comete.between(aileron, -1, 1) then
			comete.set("aileron", aileron)
		end
		if comete.between(throttle, -25, 100) then
			for i=1,4 do
				comete.set("throttle"..i, throttle)
			end
		end
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
			processdata(command, control);
		end
	end
end

local server = socket.bind(HOST, PORT)

copas.addserver(server, handler)

copas.loop()

-- END OF FILE