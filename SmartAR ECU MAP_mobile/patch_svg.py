import os

filepath = "app/src/main/assets/SmartAR_ECU_MAP_Mobile.html"
with open(filepath, "r") as f:
    html = f.read()

target = """                <div id="svgTopView" class="w-full h-full flex items-center justify-center pointer-events-none transition-opacity duration-300 filter blur-[0.5px] opacity-30">
                    <svg viewBox="0 0 800 400" class="w-full h-full" xmlns="http://www.w3.org/2000/svg">
                        <rect x="100" y="50" width="600" height="300" rx="60" fill="none" stroke="#2997ff" stroke-width="2" stroke-dasharray="8 4" />
                        <rect x="220" y="80" width="360" height="240" rx="30" fill="none" stroke="#2997ff" stroke-width="1.5" />
                        <circle cx="200" cy="80" r="35" fill="none" stroke="#323236" stroke-width="4" />
                        <circle cx="600" cy="80" r="35" fill="none" stroke="#323236" stroke-width="4" />
                        <circle cx="200" cy="320" r="35" fill="none" stroke="#323236" stroke-width="4" />
                        <circle cx="600" cy="320" r="35" fill="none" stroke="#323236" stroke-width="4" />
                        <text x="400" y="205" text-anchor="middle" fill="#8e8e93" font-family="sans-serif" font-size="14" font-weight="bold">TOP VIEW - VEHICLE PLATFORM</text>
                    </svg>
                </div>

                <div id="svgSideView" class="hidden w-full h-full flex items-center justify-center pointer-events-none transition-opacity duration-300 filter blur-[0.5px] opacity-30">
                    <svg viewBox="0 0 800 400" class="w-full h-full" xmlns="http://www.w3.org/2000/svg">
                        <path d="M 80,260 L 140,260 Q 200,160 300,140 L 500,140 Q 620,150 720,220 L 740,260 L 80,260 Z" fill="none" stroke="#2997ff" stroke-width="2" stroke-dasharray="8 4" />
                        <circle cx="200" cy="260" r="45" fill="none" stroke="#0071e3" stroke-width="3" />
                        <circle cx="600" cy="260" r="45" fill="none" stroke="#0071e3" stroke-width="3" />
                        <text x="400" y="220" text-anchor="middle" fill="#8e8e93" font-family="sans-serif" font-size="14" font-weight="bold">SIDE VIEW - VEHICLE PROFILE</text>
                    </svg>
                </div>

                <div id="svgFrontView" class="hidden w-full h-full flex items-center justify-center pointer-events-none transition-opacity duration-300 filter blur-[0.5px] opacity-30">
                    <svg viewBox="0 0 800 400" class="w-full h-full" xmlns="http://www.w3.org/2000/svg">
                        <rect x="200" y="100" width="400" height="220" rx="40" fill="none" stroke="#2997ff" stroke-width="2" stroke-dasharray="8 4" />
                        <circle cx="250" cy="160" r="25" fill="none" stroke="#2997ff" stroke-width="2" />
                        <circle cx="550" cy="160" r="25" fill="none" stroke="#2997ff" stroke-width="2" />
                        <text x="400" y="220" text-anchor="middle" fill="#8e8e93" font-family="sans-serif" font-size="14" font-weight="bold">FRONT VIEW - VEHICLE CHASSIS</text>
                    </svg>
                </div>"""

replacement = """                <div id="svgTopView" class="w-full h-full flex items-center justify-center pointer-events-none transition-opacity duration-300">
                    <img src="./car_image/Top_view.png" alt="Top View" class="w-[80%] h-[80%] object-contain drop-shadow-2xl" onerror="this.src='data:image/svg+xml;utf8,<svg viewBox=%220 0 800 400%22 xmlns=%22http://www.w3.org/2000/svg%22><text x=%22400%22 y=%22200%22 text-anchor=%22middle%22 fill=%22%238e8e93%22 font-family=%22sans-serif%22 font-size=%2214%22>TOP VIEW IMAGE NOT FOUND</text></svg>'" />
                </div>

                <div id="svgSideView" class="hidden w-full h-full flex items-center justify-center pointer-events-none transition-opacity duration-300">
                    <img src="./car_image/side_view.png" alt="Side View" class="w-[80%] h-[80%] object-contain drop-shadow-2xl" onerror="this.src='data:image/svg+xml;utf8,<svg viewBox=%220 0 800 400%22 xmlns=%22http://www.w3.org/2000/svg%22><text x=%22400%22 y=%22200%22 text-anchor=%22middle%22 fill=%22%238e8e93%22 font-family=%22sans-serif%22 font-size=%2214%22>SIDE VIEW IMAGE NOT FOUND</text></svg>'" />
                </div>

                <div id="svgFrontView" class="hidden w-full h-full flex items-center justify-center pointer-events-none transition-opacity duration-300">
                    <img src="./car_image/Front_view.png" alt="Front View" class="w-[80%] h-[80%] object-contain drop-shadow-2xl" onerror="this.src='data:image/svg+xml;utf8,<svg viewBox=%220 0 800 400%22 xmlns=%22http://www.w3.org/2000/svg%22><text x=%22400%22 y=%22200%22 text-anchor=%22middle%22 fill=%22%238e8e93%22 font-family=%22sans-serif%22 font-size=%2214%22>FRONT VIEW IMAGE NOT FOUND</text></svg>'" />
                </div>"""

if target in html:
    html = html.replace(target, replacement)
    with open(filepath, "w") as f:
        f.write(html)
    print("Replaced successfully!")
else:
    print("Target not found!")
