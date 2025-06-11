import React from "react";

const Map: React.FC = () => {
  return (
    <div id="map-area">
      <iframe
        src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3723.591298786022!2d105.74482267587284!3d21.049033087080797!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x313454ee2acd2041%3A0x159d6ede23118baa!2zTmcuIDMwIFAuIMSQw6xuaCBRdcOhbiwgxJDDrG5oIFF1w6FuLCBQaMO6YyBEaeG7hW4sIELhuq9jIFThu6sgTGnDqm0sIEjDoCBO4buZaSwgVmnhu4d0IE5hbQ!5e0!3m2!1svi!2s!4v1745352112460!5m2!1svi!2s"
        width="100%"
        height="450"
        style={{ border: 0 }}
        loading="lazy"
        title="This is a unique title"
      ></iframe>
    </div>
  );
};

export default Map;
