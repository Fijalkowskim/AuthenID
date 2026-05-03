import React from 'react';
import { Form } from 'react-bootstrap';

interface Props {
  label: string;
  options: string[];
  selected: string[];
  onChange: (selected: string[]) => void;
  maxHeight?: number;
}

export default function MultiSelectField({
  label,
  options,
  selected,
  onChange,
  maxHeight = 160,
}: Props) {
  const toggle = (value: string) => {
    if (selected.includes(value)) {
      onChange(selected.filter((v) => v !== value));
    } else {
      onChange([...selected, value]);
    }
  };

  return (
    <Form.Group className="mb-3">
      <Form.Label>{label}</Form.Label>
      <div
        className="border rounded p-2"
        style={{ maxHeight, overflowY: 'auto' }}
      >
        {options.length === 0 ? (
          <small className="text-muted">No options available</small>
        ) : (
          options.map((opt) => (
            <Form.Check
              key={opt}
              type="checkbox"
              id={`msc-${opt}`}
              label={opt}
              checked={selected.includes(opt)}
              onChange={() => toggle(opt)}
            />
          ))
        )}
      </div>
    </Form.Group>
  );
}
