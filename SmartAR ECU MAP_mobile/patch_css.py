import re

with open("app/src/main/assets/SmartAR_ECU_MAP_Mobile.html", "r") as f:
    html = f.read()

# Replace aside rules
css_target = """        aside {
            transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            will-change: transform;
        }
        @media (max-width: 767px) {
            aside.w-\\[300px\\] {
                position: fixed;
                left: 0;
                top: 64px;
                bottom: 0;
                width: 280px;
                z-index: 45;
                transform: translate3d(-100%, 0, 0);
            }
            aside.w-\\[340px\\] {
                position: fixed;
                left: 0;
                right: 0;
                bottom: 0;
                width: 100% !important;
                height: 70vh;
                z-index: 45;
                transform: translate3d(0, 100%, 0);
                border-top-left-radius: 18px;
                border-top-right-radius: 18px;
                border-left-width: 0;
                border-top-width: 1px;
            }
            aside.drawer-open {
                transform: translate3d(0, 0, 0) !important;
            }
        }"""

new_css = """        aside {
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            will-change: transform, margin, opacity;
        }
        @media (min-width: 768px) {
            #leftPanel.collapsed {
                margin-left: -300px;
                opacity: 0;
                pointer-events: none;
            }
            #rightPanel.collapsed {
                margin-right: -340px;
                opacity: 0;
                pointer-events: none;
            }
        }
        @media (max-width: 767px) {
            #leftPanel {
                position: fixed;
                left: 0;
                top: 64px;
                bottom: 0;
                width: 280px !important;
                z-index: 45;
                transform: translate3d(-100%, 0, 0);
            }
            #rightPanel {
                position: fixed;
                left: 0;
                right: 0;
                bottom: 0;
                width: 100% !important;
                height: 70vh;
                z-index: 45;
                transform: translate3d(0, 100%, 0);
                border-top-left-radius: 18px;
                border-top-right-radius: 18px;
                border-left-width: 0;
                border-top-width: 1px;
            }
            #leftPanel.drawer-open, #rightPanel.drawer-open {
                transform: translate3d(0, 0, 0) !important;
            }
        }"""

if css_target in html:
    html = html.replace(css_target, new_css)
else:
    print("CSS target not found")

with open("app/src/main/assets/SmartAR_ECU_MAP_Mobile.html", "w") as f:
    f.write(html)
